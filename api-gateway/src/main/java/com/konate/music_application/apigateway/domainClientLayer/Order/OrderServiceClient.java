package com.konate.music_application.apigateway.domainClientLayer.Order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konate.music_application.apigateway.Exceptions.*;
import com.konate.music_application.apigateway.PresentationLayer.Order.OrderRequestModel;
import com.konate.music_application.apigateway.PresentationLayer.Order.OrderResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Component
@Slf4j
public class OrderServiceClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final String ORDER_SERVICE_URL;

    public OrderServiceClient(RestTemplate restTemplate,
                              ObjectMapper mapper,
                              @Value("${app.order.host}") String orderServiceHost,
                              @Value("${app.order.port}") String orderServicePort) {
        this.restTemplate = restTemplate;
        this.objectMapper = mapper;
        this.ORDER_SERVICE_URL = "http://" + orderServiceHost + ":" + orderServicePort + "/api/v1/orders";
    }

    public OrderResponseModel getOrderById(String orderId) {
        try {
            String url = ORDER_SERVICE_URL + "/" + orderId;
            log.info("Getting order by id: {}, at OrderService URL: {}", orderId, url);
            return restTemplate.getForObject(url, OrderResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public OrderResponseModel createOrder(OrderRequestModel request) {
        try {
            String url = ORDER_SERVICE_URL;
            log.info("Creating order: {}", request);
            return restTemplate.postForObject(url, request, OrderResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public void cancelOrder(String orderId) {
        try {
            String url = ORDER_SERVICE_URL + "/" + orderId + "/cancel";
            log.info("Canceling order with id: {}, at OrderService URL: {}", orderId, url);
            restTemplate.delete(url);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public List<OrderResponseModel> getAllOrder(){
        try{
            String url = ORDER_SERVICE_URL;
            log.info("Getting all orders, at OrderService URL: {}", url);
            return Arrays.asList(restTemplate.getForObject(url, OrderResponseModel[].class));
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public OrderResponseModel updateOrder(String orderId, OrderRequestModel request) {
        try {
            String url = ORDER_SERVICE_URL + "/" + orderId;
            log.info("Updating order with id: {}, at OrderService URL: {}, with data: {}", orderId, url, request);
            restTemplate.put(url, request);
            return getOrderById(orderId); // Return the updated order
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public List<OrderResponseModel> getOrdersByCustomerId(String customerId) {
        try {
            String url = ORDER_SERVICE_URL + "/user/" + customerId;
            log.info("Getting orders for customer id: {}, at OrderService URL: {}", customerId, url);
            return Arrays.asList(restTemplate.getForObject(url, OrderResponseModel[].class));
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }



    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        if (ex.getStatusCode() == NOT_FOUND) {
            return new NotFoundException(getErrorMessage(ex));
        }
//        if (ex.getStatusCode() == UNPROCESSABLE_ENTITY) {
//            return new InconsistentAlbumException(getErrorMessage(ex));
//        }
        if (ex.getStatusCode() == BAD_REQUEST) {
            return new InvalidOrderStateException(getErrorMessage(ex));
        }
        if (ex.getStatusCode() == CONFLICT) {
            return new OrderConflictException(getErrorMessage(ex));
        }

        log.warn("Got an unexpected HTTP error from Artist Service: {}, will rethrow it", ex.getStatusCode());
        log.warn("Error body: {}", ex.getResponseBodyAsString());
        return ex;
    }


    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            // Assuming the error response body can be mapped to HttpErrorInfo
            return objectMapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            // Fallback if the error body cannot be parsed
            return ioex.getMessage();
        }
    }
}
