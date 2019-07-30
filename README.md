# bnb-wallet-springboot

README.md 작성중....

이 README를 작성중 바이낸스에서 오피셜로 돌리는 테스트넷 Node RPC부분이 504 Gateway Time-out이 뜬다.    
따라서 스케쥴링 부분 소켓타임아웃이 뜬다....

## 기존 스케쥴에서 웹소켓으로 변경

일단 위와 같은 상황이 발생해서 차라리 api호출로 트랜잭션 정보들을 읽어와서 해당 입금된 정보를 DB로 저장하게 만듬.

## ReactorNettyWebSocketClient

WebFlux를 위해 ReactorNettyWebSocketClient을 활용한 소켓 통신으로 관련 정보를 리스너하고 있다가 이벤트 발생시 메세지를 받아서 처리하게 변경.


현재는 이 부분을 그냥 HttpURLConnection으로 처리하고 있지만 차후 WebClient를 활용해서 만들 예정.


## At A Glance

원래는 WebClient를 활용했지만 어떤 이유에서인지 객체로 변환하다 에러가 발생한다.
특히 HttpURLConnection의 경우에도 ObjectMapper를 활용해서 변환하면 에러가 발생!?
그래서 귀찮지만 Gson으로 변환 (이건 에러가 안나네?? 잭슨이 아저씨 배반인가?? 아니면 내가 뭘 잘못했나??)

--> 잘못함. 테스트 결과 Gson의 경우 json형태의 스트링 콘텐츠에서 매핑한 객체에 없는 키값이 들어와도 무시하고 있는 키값에 맞추서 맵핑해준다.    
근데 잭슨이는 에러를 밷어내는데 어노테이션으로 그 부분을 무시하게 설정을 따로 해줘야 한다....    
