package com.example.orderingmanager.Dto;

import com.example.orderingmanager.Dto.request.OwnerSignUpDto;
import com.example.orderingmanager.Dto.request.SignInDto;
import com.example.orderingmanager.Dto.response.OwnerSignInResultDto;

import javax.annotation.Nullable;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

   	@Multipart
   	@PUT("/api/restaurant/{restaurantId}/food/{foodId}")
	Call<ResultDto<Boolean>> putFood(@Path("restaurantId") Long restaurantId, @Path("foodId") Long foodId, @Part(value = "dto") FoodDto foodDto);

   	@Multipart
   	@PUT("/api/restaurant/{restaurantId}/food/{foodId}")
	Call<ResultDto<Boolean>> putFood(@Path("restaurantId") Long restaurantId, @Path("foodId") Long foodId, @Part(value = "dto") FoodDto foodDto,  @Part MultipartBody.Part file);

	// 서버 내 데이터 삭제
	@DELETE("/api/restaurant/food/{foodId}")
	Call<ResultDto<Boolean>> deleteFood(@Path("foodId") Long foodId);
}