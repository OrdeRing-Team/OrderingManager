package com.example.orderingmanager.Dto;

import com.example.orderingmanager.Dto.request.OwnerSignUpDto;
import com.example.orderingmanager.Dto.request.SignInDto;
import com.example.orderingmanager.Dto.response.OwnerSignInResultDto;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RetrofitService {

   	@POST("/api/owner/signin")
	Call<ResultDto<OwnerSignInResultDto>> ownerSignIn(@Body SignInDto signInDto);

	@POST("/api/owner/signup")
	Call<ResultDto<Long>> ownerSignUp(@Body OwnerSignUpDto ownerSignUpDto);

   	@Multipart
   	@POST("/api/restaurant/{restaurantId}/food")
	Call<ResultDto<Long>> addFood(@Path("restaurantId") Long id, @Part(value = "dto") FoodDto foodDto, @Part MultipartBody.Part file);
}