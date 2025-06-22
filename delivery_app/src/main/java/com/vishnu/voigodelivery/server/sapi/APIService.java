package com.vishnu.voigodelivery.server.sapi;

import com.google.gson.JsonObject;
import com.vishnu.voigodelivery.miscellaneous.StartDutyModel;
import com.vishnu.voigodelivery.miscellaneous.EndDutyModel;
import com.vishnu.voigodelivery.miscellaneous.DutySettingsModel;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface APIService {

    @Multipart
    @POST("register-delivery-account")
    Call<JsonObject>
    registerUser(
//            @Part MultipartBody.Part image,
            @Part("account_data") RequestBody accountData);

    @POST("start-duty/")
    Call<JsonObject> startDuty(@Body StartDutyModel startDutyModel);

    @POST("update-duty-data")
    Call<JsonObject> updateDutySettingsData(@Body DutySettingsModel dutySettingsModel);

    @POST("end-duty/")
    Call<JsonObject> endDuty(@Body EndDutyModel endDutyModel);

    @GET("get-duty-status/{delivery_partner_id}")
    Call<JsonObject> getDutyStatus(
            @Path("delivery_partner_id") String deliveryPartnerID);

    @GET("get-delivery-client-data/{client_id}")
    Call<JsonObject> getUserData(
            @Path("client_id") String clientID);

    @GET("set-current-order/{delivery_partner_id}/{user_id}/{order_id}")
    Call<JsonObject> setCurrentDeliveryOrder(
            @Path("delivery_partner_id") String deliveryPartnerID,
            @Path("user_id") String userID,
            @Path("order_id") String orderID);

    @GET("decline-order/{delivery_partner_id}/{user_id}/{order_id}")
    Call<JsonObject> declineDeliveryOrder(
            @Path("delivery_partner_id") String deliveryPartnerID,
            @Path("user_id") String userID,
            @Path("order_id") String orderID);

    @GET("keep-it-order/{delivery_partner_id}/{user_id}/{order_id}")
    Call<JsonObject> keepItDeliveryOrder(
            @Path("delivery_partner_id") String deliveryPartnerID,
            @Path("user_id") String userID,
            @Path("order_id") String orderID);

    @GET("reached-shop/{delivery_partner_id}/{user_id}/{order_id}")
    Call<JsonObject> reachedShop(
            @Path("delivery_partner_id") String deliveryPartnerID,
            @Path("user_id") String userID,
            @Path("order_id") String orderID);

    @GET("order-enroute/{delivery_partner_id}/{user_id}/{total_amount}/{order_id}")
    Call<JsonObject> orderEnroute(
            @Path("delivery_partner_id") String deliveryPartnerID,
            @Path("user_id") String userID,
            @Path("total_amount") String totalAmount,
            @Path("order_id") String orderID);

    @GET("order-pickedup/{delivery_partner_id}/{user_id}/{order_id}")
    Call<JsonObject> orderPickedUp(
            @Path("delivery_partner_id") String deliveryPartnerID,
            @Path("user_id") String userID,
            @Path("order_id") String orderID);

    @GET("order-delivered/{delivery_partner_id}/{user_id}/{order_by_voice_doc_id}/{order_by_voice_audio_ref_id}/{order_id}")
    Call<JsonObject> orderDelivered(
            @Path("delivery_partner_id") String deliveryPartnerID,
            @Path("user_id") String userID,
            @Path("order_by_voice_doc_id") String orderByVoiceDocID,
            @Path("order_by_voice_audio_ref_id") String orderByVoiceAudioRefID,
            @Path("order_id") String orderID);

    @GET("get-voice-order-data/{user_id}/{order_by_voice_type}/{order_by_voice_doc_id}/{order_by_voice_audio_ref_id}/{shop_id}")
    Call<JsonObject> getVoiceOrderData(
            @Path("user_id") String userId,
            @Path("order_by_voice_type") String orderByVoiceType,
            @Path("order_by_voice_doc_id") String orderByVoiceDocID,
            @Path("order_by_voice_audio_ref_id") String orderByVoiceAudioRefID,
            @Path("shop_id") String shopID);

    @FormUrlEncoded
    @POST("fetch-order-data/")
    Call<JsonObject> fetchOrderData(
            @Field("order_type") String orderType,
            @Field("user_id") String userId,
            @Field("dp_id") String dpID,
            @Field("shop_id") String shopID,
            @Field("shop_name") String shopName,
            @Field("user_phno") String userPhno,
            @Field("order_key") String orderKey);
}


