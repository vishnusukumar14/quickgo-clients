package com.vishnu.voigovendor.callbacks;

public interface PincodeValidation {
    void onSuccess(boolean isPinValid, String postOffName, String cityName);
}

