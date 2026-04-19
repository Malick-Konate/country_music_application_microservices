package com.konate.music_application.ordersubdomain.MappingLayer;
import com.konate.music_application.ordersubdomain.DataLayer.Order;
import com.konate.music_application.ordersubdomain.DataLayer.OrderIdentifier;
import com.konate.music_application.ordersubdomain.PresentationLayer.OrderRequestModel;
import com.konate.music_application.ordersubdomain.domainClientLayer.User.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface OrderRequestMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(expression = "java(orderIdentifier)", target = "orderIdentifier"),
//            @Mapping(source = )
//            @Mapping(expression = "java(userIdentifier)", target = "userIdentifier"),
            @Mapping(source = "requestModel.orderStatus", target = "orderStatus"),
            @Mapping(source = "requestModel.orderItems", target = "orderItems"),
            @Mapping(source = "requestModel.payments", target = "payments")

    })
    Order toOder(OrderRequestModel requestModel,
                 OrderIdentifier orderIdentifier,
                 UserModel userModel);
}

