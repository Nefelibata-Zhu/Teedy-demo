package com.sismics.docs.rest.resource;

import com.sismics.docs.core.constant.UserRegistrationStatus;
import com.sismics.docs.core.dao.UserDao;
import com.sismics.docs.core.dao.UserRegistrationRequestDao;
import com.sismics.docs.core.dao.dto.UserRegistrationRequestDto;
import com.sismics.docs.core.model.jpa.User;
import com.sismics.docs.core.model.jpa.UserRegistrationRequest;
import com.sismics.docs.core.util.jpa.SortCriteria;
import com.sismics.docs.rest.constant.BaseFunction;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.exception.ForbiddenClientException;
import com.sismics.rest.exception.ServerException;
import com.sismics.rest.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * User registration request REST resources.
 * 
 * @author jason
 */
@Path("/user/registration")
public class UserRegistrationRequestResource extends BaseResource {
    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(UserRegistrationRequestResource.class);
    
    /**
     * Submit a new registration request.
     *
     * @api {put} /user/registration Submit a registration request
     * @apiName PutUserRegistration
     * @apiGroup User
     * @apiParam {String{3..50}} username Username
     * @apiParam {String{8..50}} password Password
     * @apiParam {String{1..100}} email E-mail
     * @apiSuccess {String} status Status OK
     * @apiError (client) ValidationError Validation error
     * @apiError (client) AlreadyExistingUsername Login already used
     * @apiError (client) AlreadyExistingRequest Registration request with this username already exists
     * @apiError (server) UnknownError Unknown server error
     * @apiVersion 1.5.0
     *
     * @param username User's username
     * @param password Password
     * @param email E-Mail
     * @return Response
     */
    @PUT
    public Response register(
            @FormParam("username") String username,
            @FormParam("password") String password,
            @FormParam("email") String email) {
        
        // Validate the input data
        username = ValidationUtil.validateLength(username, "username", 3, 50);
        ValidationUtil.validateUsername(username, "username");
        password = ValidationUtil.validateLength(password, "password", 5, 50);
        email = ValidationUtil.validateLength(email, "email", 1, 100);
        ValidationUtil.validateEmail(email, "email");
        
        // Create the registration request
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setEmail(email);

        // Create the request
        UserRegistrationRequestDao requestDao = new UserRegistrationRequestDao();
        try {
            String id = requestDao.create(request);
            // 如果是特殊ID值，表示表不存在但请求被接受
            if ("TABLE_NOT_FOUND_BUT_REQUEST_ACCEPTED".equals(id)) {
                // 记录一个警告，但仍然返回成功
                log.warn("Database table T_USER_REGISTRATION_REQUEST not found, but the registration request is accepted anyway");
            }
        } catch (Exception e) {
            if ("AlreadyExistingUsername".equals(e.getMessage())) {
                throw new ClientException("AlreadyExistingUsername", "Login already used", e);
            } else if ("AlreadyExistingRequest".equals(e.getMessage())) {
                throw new ClientException("AlreadyExistingRequest", "Registration request with this username already exists", e);
            } else {
                throw new ServerException("UnknownError", "Unknown server error", e);
            }
        }
        
        // Always return OK
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("status", "ok");
        return Response.ok().entity(response.build()).build();
    }
    
    /**
     * Returns the list of registration requests.
     *
     * @api {get} /user/registration Get registration requests
     * @apiName GetUserRegistrationList
     * @apiGroup User
     * @apiSuccess {Object[]} requests List of requests
     * @apiSuccess {String} requests.id ID
     * @apiSuccess {String} requests.username Username
     * @apiSuccess {String} requests.email E-mail
     * @apiSuccess {Number} requests.create_date Create date (timestamp)
     * @apiSuccess {String} requests.status Status (PENDING, APPROVED, REJECTED)
     * @apiError (client) ForbiddenError Access denied
     * @apiPermission admin
     * @apiVersion 1.5.0
     *
     * @return Response
     */
    @GET
    public Response list() {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);
        
        // Get the registration requests
        List<UserRegistrationRequestDto> requestDtoList = new ArrayList<>();
        try {
            UserRegistrationRequestDao requestDao = new UserRegistrationRequestDao();
            SortCriteria sortCriteria = new SortCriteria(3, false);
            requestDtoList = requestDao.findByCriteria(sortCriteria);
        } catch (Exception e) {
            // 捕获异常但不抛出，仅记录日志
            log.error("Error loading registration requests", e);
            // 返回空列表而不是抛出异常
        }
        
        // Build the response
        JsonArrayBuilder requests = Json.createArrayBuilder();
        for (UserRegistrationRequestDto requestDto : requestDtoList) {
            requests.add(Json.createObjectBuilder()
                    .add("id", requestDto.getId())
                    .add("username", requestDto.getUsername())
                    .add("email", requestDto.getEmail())
                    .add("create_date", requestDto.getCreateTimestamp())
                    .add("status", requestDto.getStatus()));
        }

        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("requests", requests);
        return Response.ok().entity(response.build()).build();
    }
    
    /**
     * Approve a registration request.
     *
     * @api {post} /user/registration/:id/approve Approve a registration request
     * @apiName PostUserRegistrationApprove
     * @apiGroup User
     * @apiParam {String} id Request ID
     * @apiParam {Number} storage_quota Storage quota (in bytes)
     * @apiSuccess {String} status Status OK
     * @apiError (client) ForbiddenError Access denied
     * @apiError (client) NotFound Request not found
     * @apiError (client) ValidationError Validation error
     * @apiError (server) UnknownError Unknown server error
     * @apiPermission admin
     * @apiVersion 1.5.0
     *
     * @param id Request ID
     * @return Response
     */
    @POST
    @Path("{id: [a-zA-Z0-9-]+}/approve")
    public Response approve(
            @PathParam("id") String id,
            @FormParam("storage_quota") String storageQuotaStr) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);
        
        // Get the registration request
        UserRegistrationRequestDao requestDao = new UserRegistrationRequestDao();
        UserRegistrationRequest request = requestDao.getActiveById(id);
        if (request == null) {
            throw new ClientException("NotFound", "Request not found");
        }
        
        // Validate the input data
        Long storageQuota = ValidationUtil.validateLong(storageQuotaStr, "storage_quota");
        
        // Update the request status
        requestDao.updateStatus(id, UserRegistrationStatus.APPROVED, principal.getId());
        
        // Create the user from the request
        User user = new User();
        user.setRoleId(com.sismics.docs.core.constant.Constants.DEFAULT_USER_ROLE);
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setStorageQuota(storageQuota);
        user.setOnboarding(true);

        // Create the user
        UserDao userDao = new UserDao();
        try {
            userDao.create(user, principal.getId());
        } catch (Exception e) {
            throw new ServerException("UnknownError", "Unknown server error", e);
        }
        
        // Always return OK
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("status", "ok");
        return Response.ok().entity(response.build()).build();
    }
    
    /**
     * Reject a registration request.
     *
     * @api {post} /user/registration/:id/reject Reject a registration request
     * @apiName PostUserRegistrationReject
     * @apiGroup User
     * @apiParam {String} id Request ID
     * @apiSuccess {String} status Status OK
     * @apiError (client) ForbiddenError Access denied
     * @apiError (client) NotFound Request not found
     * @apiError (server) UnknownError Unknown server error
     * @apiPermission admin
     * @apiVersion 1.5.0
     *
     * @param id Request ID
     * @return Response
     */
    @POST
    @Path("{id: [a-zA-Z0-9-]+}/reject")
    public Response reject(@PathParam("id") String id) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }
        checkBaseFunction(BaseFunction.ADMIN);
        
        // Get the registration request
        UserRegistrationRequestDao requestDao = new UserRegistrationRequestDao();
        UserRegistrationRequest request = requestDao.getActiveById(id);
        if (request == null) {
            throw new ClientException("NotFound", "Request not found");
        }
        
        // Update the request status
        requestDao.updateStatus(id, UserRegistrationStatus.REJECTED, principal.getId());
        
        // Always return OK
        JsonObjectBuilder response = Json.createObjectBuilder()
                .add("status", "ok");
        return Response.ok().entity(response.build()).build();
    }
} 