package io.basquiat.bnb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.basquiat.bnb.domain.RequestWithdraw;
import io.basquiat.bnb.domain.entity.BlockTransactions;
import io.basquiat.bnb.domain.response.AddressResponse;
import io.basquiat.bnb.domain.response.BalanceResponse;
import io.basquiat.bnb.domain.response.ValidationAddressResponse;
import io.basquiat.bnb.domain.response.WithdrawResponse;
import io.basquiat.bnb.repository.BlockTransactionsRepository;
import io.basquiat.bnb.service.AsyncService;
import io.basquiat.bnb.service.BnbService;
import io.basquiat.common.code.ResultCode;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 
 * API Controller
 * 
 * created by basquiat
 *
 */
@RestController
@RequestMapping("")
@Api(value = "API Controller", tags = {"Binance Coin API Controller"})
@Slf4j
public class ApiController {

	@Autowired
	private BnbService bnbService;

	@Autowired
	private AsyncService async;
	
	@Autowired
	private BlockTransactionsRepository blockTransactionsRepo;
	
	/**
	 * 테스트용
	 * @return Flux<BlockTransactions>
	 */
	@GetMapping("/find")
	public Flux<BlockTransactions> find() {
		return async.excuteJDBC(() -> blockTransactionsRepo.findAll()).flatMapMany(Flux::fromIterable);
	}
	
	/**
	 * 주소 생성하기 요청
	 * @return Mono<AddressResponse>
	 */
	@GetMapping("/address/create")
	public Mono<AddressResponse> createAddress() {
		log.info("[Create Address] START");
		return bnbService.createAddress()
						 .onErrorResume(throwable ->
									{
										return Mono.just(AddressResponse.builder()
																		.resultCode(ResultCode.CREATEWALLET_ERROR.statusCode)
																		.message(ResultCode.CREATEWALLET_ERROR.message)
																		.build()
														);
									}
						 );
		
	}

	/**
	 * 주소 유효성 체크 요청
	 * @param address
	 * @return Mono<ValidationAddressResponse>
	 */
	@GetMapping("/address/check/{address}")
	public Mono<ValidationAddressResponse> checkAddress(@PathVariable(value = "address", required = true) String address) {
		log.info("[Create Address Validation Check] START");
		return bnbService.checkAddress(address);
	}

	/**
	 * 출금지갑 잔고 조회 요청 
	 * @return Mono<BalanceResponse>
	 */
	@GetMapping("/address/balance")
	public Mono<BalanceResponse> balanceOf() {
		log.info("[Inquire Address Balance] START");
		return bnbService.getBalanceOf()
						 .onErrorResume(throwable ->
												{																
													return Mono.just(BalanceResponse.builder()
																		    		.resultCode(ResultCode.BALANCE_ERROR.statusCode)
																		    		.message(ResultCode.BALANCE_ERROR.message)
																		    		.build()
													    			);
												}
						 );
	}

	/**
	 * 출금요청 
	 * @param requestWithdraw
	 * @return Mono<WithdrawResponse>
	 */
	@PostMapping("/withdraw")
	public Mono<WithdrawResponse> withdraw(@RequestBody RequestWithdraw requestWithdraw) {
		log.info("[Withdraw Binance Coin] START");
		return bnbService.startWithdraw(requestWithdraw)
						 .onErrorResume(throwable ->
												{																
													return Mono.just(WithdrawResponse.builder()
																				     .resultCode(ResultCode.WITHDRAW_ERROR.statusCode)
																				     .message(throwable.getMessage())
																				     .build());
												}
						 );
	}

}
