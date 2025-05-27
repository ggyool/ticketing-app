# 티케팅 서비스
학습용 티케팅 백엔드 서버 (이벤트 기반 통신, 분산락, 분산 트랜잭션 학습 목적)

## 목표
- 도메인별 서비스 분리
- 이벤트 기반 비동기 통신
- 사가를 이용한 분산 트랜잭션 처리
- 분산락을 이용한 동시성 제어
- 중복 이벤트, 실패 이벤트 처리

## 각 모듈 역할 및 주요 기능
1. **event-common**
   - 사가 및 이벤트 구조 추상화
2. servlet-ticketing-service
   - 티켓 예약 API
   - 티켓 진행 API
   - 티켓 진행 상황 조회 API
3. servlet-worker-service
   - 티켓 진행 사가 제어
   - Event(행사) 도메인 이벤트 소비 
   - Payment 도메인 이벤트 소비
   - Ticket 도메인 이벤트 발급
4. servlet-event-service
   - Event 도메인 CRU API 
   - Event 도메인 이벤트 발급
5. user-worker-service
   - 이벤트 기반 여러 기능 제공
     - 활성 유저 체크 기능
     - 유저 포인트 사용 기능
     - 이상 유저 체크 기능
6. servlet-payment-service
   - 최종 결제 완료 API
7. payment-worker-service
   - 이벤트 기반 기능 제공
     - 결제 생성 기능
   - Payment 도메인 이벤트 발급


## 티케팅 대략적인 흐름

### 1. 예약 과정
티켓 발급 전 티켓을 선점하는 과정
1. 아래 API 호출  
POST servlet-ticketing-service/ticketing/reserve
2. Redis Hash에 TTL 설정하여 결제 대기 상태로 저장

### 2. 티켓 발급 과정
1. 클라이언트 -> API 호출
- [servlet-ticketing-service] POST servlet-ticketing-service/ticketing/process
- [servlet-ticketing-service] ticketing.process 이벤트 발급
2. 티케팅 진행 (학습용으로 여러 사가 스텝을 가지게끔 하기 위해 현실에 맞지 않는 스텝을 가질 수 있음, 실제 결제 전까지의 과정)
- [ticketing-worker-service] ticketing.process 소비하여 사가 호출
- [user-worker-service] user.check.active.request 소비하고 처리 후 user.check.active.response 발급
- [ticketing-worker-service] user.check.active.response 소비하여 진행 결정 및 user.reserve.point.request 이벤트 발급 
- [user-worker-service] user.reserve.point.request 소비하고 처리 후 user.reserve.point.response 발급 
- [ticketing-worker-service] user.reserve.point.response 소비하여 진행 결정 및 user.check.fraud.request 이벤트 발급
- [user-worker-service] user.check.fraud.request 소비하고 처리 후 user.check.fraud.response 발급
- [ticketing-worker-service] user.check.fraud.response 소비하여 진행 결정 및 payment.create.request 이벤트 발급
- [payment-worker-service] payment.create.request 소비하여 처리 후 payment.create.response 이벤트 발급 (PaymentStatus.CREATED 상태)
- [ticketing-worker-service] payment.create.response 소비하여 진행 결정 및 사가 마무리
3. 클라이언트 -> API 호출하여 사가 완료 확인 후 결제 진행  
- [servlet-ticketing-service] GET servlet-ticketing-service/process/{ticketId}  
- [client] PG사의 결제 모듈 호출하여 결제 유도
4. 결제 완료 후 API 호출
- [servlet-payment-service] POST servlet-payment-service/payment/confirmed
- [servlet-payment-service] 최소 유효성 검사 후 payment.confirmed.request 이벤트 발급
- [payment-worker-service] payment.confirmed.request 소비하여 pg 사의 paymentId 세팅 + PaymentStatus.SUCCEED or PaymentStatus.FAILED 로 저장
- [payment-worker-service] domain.payment 결제 도메인 이벤트 발급 (type = 'update')  
- [ticketing-worker-service] type = 'update' 인 domain.payment 소비하여 티켓 발급 