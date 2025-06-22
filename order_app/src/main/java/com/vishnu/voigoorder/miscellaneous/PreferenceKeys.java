package com.vishnu.voigoorder.miscellaneous;

public class PreferenceKeys {

    // Prevent instantiation
    private PreferenceKeys() {
        throw new AssertionError("No instances for you!");
    }

    public static final String HOME_RECOMMENDATION_FRAGMENT_ORDER_ID = "homeRecommendationFragmentOrderID";
    public static final String HOME_RECOMMENDATION_FRAGMENT_AUDIO_REF_ID = "homeRecommendationFragmentAudioRefID";

    public static final String HOME_ORDER_BY_VOICE_FRAGMENT_ORDER_ID = "homeOrderByVoiceFragmentOrderID";
    public static final String HOME_ORDER_BY_VOICE_FRAGMENT_AUDIO_REF_ID = "homeOrderByVoiceFragmentAudioRefID";

    public static final String HOME_ORDER_BY_VOICE_SELECTED_ADDRESS_KEY = "homeOrderByVoiceSelectedAddressKey";
    public static final String HOME_ORDER_BY_VOICE_SELECTED_ADDRESS_TYPE = "homeOrderByVoiceFragmentSelectedAddressType";
    public static final String HOME_ORDER_BY_VOICE_SELECTED_ADDRESS_STREET_ADDRESS = "homeOrderByVoiceFragmentSelectedAddressStreetAddr";
    public static final String HOME_ORDER_BY_VOICE_SELECTED_ADDRESS_FULL_ADDRESS = "homeOrderByVoiceFragmentSelectedAddressFullAddr";

    public static final String HOME_RECOMMENDATION_SELECTED_ADDRESS_KEY = "homeRecommendationSelectedAddressKey";
    public static final String HOME_RECOMMENDATION_SELECTED_ADDRESS_TYPE = "HomeRecommendationFragmentSelectedAddressType";
    public static final String HOME_RECOMMENDATION_SELECTED_ADDRESS_STREET_ADDRESS = "homeRecommendationFragmentSelectedAddressStreetAddr";
    public static final String HOME_RECOMMENDATION_SELECTED_ADDRESS_FULL_ADDRESS = "homeRecommendationFragmentSelectedAddressFullAddr";
    public static final String HOME_RECOMMENDATION_CURRENT_SHOP_PINCODE = "homeRecommendationFragmentCurrentShopPincode";

    public static final String IS_SET_TO_CURRENT_LOCATION = "isSetToCurrentLoc";


}
