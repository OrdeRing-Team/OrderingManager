package com.example.orderingmanager.Dto;

import com.example.orderingmanager.Dto.request.MessageDto;
import com.example.orderingmanager.Dto.request.OwnerSignUpDto;
import com.example.orderingmanager.Dto.request.PasswordChangeDto;
import com.example.orderingmanager.Dto.request.PhoneNumberDto;
import com.example.orderingmanager.Dto.request.RestaurantDataWithLocationDto;
import com.example.orderingmanager.Dto.request.SalesRequestDto;
import com.example.orderingmanager.Dto.request.SignInDto;
import com.example.orderingmanager.Dto.request.VerificationDto;
import com.example.orderingmanager.Dto.request.WaitingTimeDto;
import com.example.orderingmanager.Dto.response.OrderPreviewDto;
import com.example.orderingmanager.Dto.response.OwnerSignInResultDto;
import com.example.orderingmanager.Dto.response.RepresentativeMenuDto;
import com.example.orderingmanager.Dto.response.RestaurantInfoDto;
import com.example.orderingmanager.Dto.response.ReviewPreviewDto;
import com.example.orderingmanager.Dto.response.SalesResponseDto;
import com.example.orderingmanager.Dto.response.WaitingPreviewDto;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {

	// 로그인
   	@POST("/api/owner/signin")
	Call<ResultDto<OwnerSignInResultDto>> ownerSignIn(@Body SignInDto signInDto);

   	// 회원가입
	@POST("/api/owner/signup")
	Call<ResultDto<Long>> ownerSignUp(@Body OwnerSignUpDto ownerSignUpDto);

	// 매장등록
	@POST("/api/owner/{owner_id}/restaurant")
	Call<ResultDto<Long>> registerStore(@Path("owner_id") Long ownerId, @Body RestaurantDataWithLocationDto restaurantDataWithLocationDto);

	// 매장정보 변경
	@PUT("/api/restaurant/{restaurant_id}")
	Call<ResultDto<Boolean>> modifyStore(@Path("restaurant_id") Long restaurantId, @Body RestaurantDataWithLocationDto restaurantDataWithLocationDto);

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

	// 매장 공지사항, 좌표 가져오기
	@GET("/api/restaurant/{restaurant_id}/info")
	Call<ResultDto<RestaurantInfoDto>> getStoreNoticeAndCoordinate(@Path("restaurant_id") Long restaurantId);

	// 매장 공지 작성/수정 API
	@PUT("/api/restaurant/{restaurant_id}/notice")
	Call<ResultDto<Boolean>> saveNotice(@Path("restaurant_id") Long restaurantId, @Body MessageDto messageDto);

	// 서버 내 메뉴 데이터 삭제
	@DELETE("/api/restaurant/food/{foodId}")
	Call<ResultDto<Boolean>> deleteFood(@Path("foodId") Long foodId);

	// 대표메뉴 추가
	@POST("/api/restaurant/{restaurant_id}/representative")
	Call<ResultDto<Boolean>> setRepresent(@Path("restaurant_id") Long restaurantId,
											@Query(value = "food_id") Long foodId);

	// 대표메뉴 삭제
	@DELETE("/api/restaurant/representative/{representative_id}")
	Call<ResultDto<Boolean>> deleteRepresent(@Path("representative_id") Long representativeId);

	// 대표메뉴 리스트 가져오기
	@GET("/api/restaurant/{restaurant_id}/representatives")
	Call<ResultDto<List<RepresentativeMenuDto>>> getRepresentList(@Path("restaurant_id") Long restaurantId);

	// 진행중인 주문 리스트 가져오기
	@GET("/api/restaurant/{restaurantId}/orders/ongoing")
	Call<ResultDto<List<OrderPreviewDto>>> getOrderReceivedList(@Path("restaurantId") Long restaurantId);

	// 처리된 주문 리스트 가져오기
	@GET("/api/restaurant/{restaurantId}/orders/finished")
	Call<ResultDto<List<OrderPreviewDto>>> getOrderProcessedList(@Path("restaurantId") Long restaurantId);

	// 점주 주문 취소
	@POST("/api/order/{order_id}/owner_cancel")
	Call<ResultDto<OrderPreviewDto>> cancelOrder(@Path("order_id") Long orderId, @Body MessageDto messageDto);

	// 점주 주문 체크(승인)
	@POST("/api/order/{order_id}/check")
	Call<ResultDto<OrderPreviewDto>> acceptOrder(@Path("order_id") Long orderId);

	// 점주 주문 완료처리
	@POST("/api/order/{order_id}/complete")
	Call<ResultDto<OrderPreviewDto>> completeOrder(@Path("order_id") Long orderId);

	// 회원탈퇴
	@DELETE("/api/owner/{ownerId}")
	Call<ResultDto<Boolean>> deleteaccount(@Path("ownerId") Long ownerId);

	// 매장 한달 매출 불러오기
	@POST("/api/restaurant/{restaurantId}/monthly_sales")
	Call<ResultDto<List<SalesResponseDto>>> getSalesMontly(@Path("restaurantId") Long restaurantId, @Body SalesRequestDto salesRequestDto);

	// 매장 특정 달의 일별 매출 불러오기
	@POST("/api/restaurant/{restaurantId}/daily_sales")
	Call<ResultDto<List<SalesResponseDto>>> getSalesDaily(@Path("restaurantId") Long restaurantId, @Body SalesRequestDto salesRequestDto);



	// 웨이팅 요청 리스트 불러오기
	@POST("/api/restaurant/{restaurant_id}/waitings")
	Call<ResultDto<List<WaitingPreviewDto>>> getWaitingList(@Path("restaurant_id") Long restaurant_id);

	// 웨이팅 취소
	@DELETE("/api/waiting/{waiting_id}")
	Call<ResultDto<Boolean>> deleteWaiting(@Path("waiting_id") Long waiting_id);

	// 웨이팅 호출
	@DELETE("/api/restaurant/waiting/{waitingId}/call")
	Call<ResultDto<Boolean>> callWaiting(@Path("waitingId") Long waitingId);

	// 입장 대기 시간 설정
	@PUT("/api/restaurant/{restaurant_id}/admission_waiting_time")
	Call<ResultDto<Boolean>> setWaitingTime(@Path("restaurant_id") Long restaurant_id, @Body WaitingTimeDto waitingTimeDto);

	// 포장주문 대기 시간 설정
	@PUT("/api/restaurant/{restaurant_id}/order_waiting_time")
	Call<ResultDto<Boolean>> setOrderingWaitingTime(@Path("restaurant_id") Long restaurant_id, @Body WaitingTimeDto waitingTimeDto);

	// 매장 리뷰 리스트 조회 API
	@GET("/api/restaurant/{restaurantId}/reviews")
	Call<ResultDto<List<ReviewPreviewDto>>> getReviewList(@Path("restaurantId") Long restaurantId);

}