package com.example.orderingmanager.Dto;

import com.example.orderingmanager.Dto.request.OwnerSignUpDto;
import com.example.orderingmanager.Dto.request.PasswordChangeDto;
import com.example.orderingmanager.Dto.request.PhoneNumberDto;
import com.example.orderingmanager.Dto.request.SalesRequestDto;
import com.example.orderingmanager.Dto.request.SignInDto;
import com.example.orderingmanager.Dto.request.VerificationDto;
import com.example.orderingmanager.Dto.response.DailySalesDto;
import com.example.orderingmanager.Dto.response.OwnerSignInResultDto;
import com.example.orderingmanager.Dto.response.SalesResponseDto;

import java.util.List;

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

	// 로그인
   	@POST("/api/owner/signin")
	Call<ResultDto<OwnerSignInResultDto>> ownerSignIn(@Body SignInDto signInDto);

   	// 회원가입
	@POST("/api/owner/signup")
	Call<ResultDto<Long>> ownerSignUp(@Body OwnerSignUpDto ownerSignUpDto);

	// 인증요청
	@POST("/api/owner/verification/get")
	Call<ResultDto<Boolean>> phoneNumber(@Body PhoneNumberDto phoneNumberDto);

	// 인증번호 확인
	@POST("/api/owner/verification/check")
	Call<ResultDto<Boolean>> verification(@Body VerificationDto verificationDto);

	// 비밀번호 변경
	@PUT("/api/owner/{owner_id}/password")
	Call<ResultDto<Boolean>> changePassword(@Path("owner_id") Long ownerId, @Body PasswordChangeDto passwordChangeDto);

	// 전화번호 변경
	@PUT("/api/owner/{owner_id}/phone_number")
	Call<ResultDto<Boolean>> reverify(@Path("owner_id") Long ownerId, @Body PhoneNumberDto phoneNumberDto);

	// 매장 아이콘 변경
	@Multipart
	@PUT("/api/restaurant/{restaurant_id}/profile_image")
	Call<ResultDto<Boolean>> putStoreIcon(@Path("restaurant_id") Long restaurantId, @Part MultipartBody.Part file);


	// 매장 대표 메뉴 변경
	@Multipart
	@PUT("/api/restaurant/{restaurant_id}/background_image")
	Call<ResultDto<Boolean>> putStoreSigMenu(@Path("restaurant_id") Long restaurantId, @Part MultipartBody.Part file);

	// 메뉴 추가
   	@Multipart
   	@POST("/api/restaurant/{restaurantId}/food")
	Call<ResultDto<Long>> addFood(@Path("restaurantId") Long id, @Part(value = "dto") FoodDto foodDto, @Part MultipartBody.Part file);

   	// 메뉴 수정
   	@Multipart
   	@PUT("/api/restaurant/{restaurantId}/food/{foodId}")
	Call<ResultDto<Boolean>> putFood(@Path("restaurantId") Long restaurantId, @Path("foodId") Long foodId, @Part(value = "dto") FoodDto foodDto);

   	// 메뉴 수정(이미지 포함)
   	@Multipart
   	@PUT("/api/restaurant/{restaurantId}/food/{foodId}")
	Call<ResultDto<Boolean>> putFood(@Path("restaurantId") Long restaurantId, @Path("foodId") Long foodId, @Part(value = "dto") FoodDto foodDto,  @Part MultipartBody.Part file);

   	// 메뉴 품절 정보 PUT
	@PUT("/api/restaurant/food/{food_id}/status")
	Call<ResultDto<Boolean>> putSoldout(@Path("food_id") Long foodId, @Body FoodStatusDto foodStatusDto);

	// 매장 모든 음식 불러오기
	@POST("/api/restaurant/{restaurantId}/foods")
	Call<ResultDto<List<FoodDto>>> getFood(@Path("restaurantId") Long restaurantId);

	// 서버 내 메뉴 데이터 삭제
	@DELETE("/api/restaurant/food/{foodId}")
	Call<ResultDto<Boolean>> deleteFood(@Path("foodId") Long foodId);

	// 회원탈퇴
	@DELETE("/api/owner/{ownerId}")
	Call<ResultDto<Boolean>> deleteaccount(@Path("ownerId") Long ownerId);

	// 매장 한달 매출 불러오기
	@POST("/api/restaurant/{restaurantId}/monthly_sales")
	Call<ResultDto<List<SalesResponseDto>>> getSalesMontly(@Path("restaurantId") Long restaurant_id, @Body SalesRequestDto salesRequestDto);

	// 매장 특정 달의 일별 매출 불러오기
	@POST("/api/restaurant/{restaurantId}/daily_sales")
	Call<ResultDto<List<SalesResponseDto>>> getSalesDaily(@Path("restaurantId") Long restaurantId, @Body SalesRequestDto salesRequestDto);



}