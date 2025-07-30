![avalanche.png](docs%2Fimages%2Favalanche.png)

# PROJECT AVALANCHE 

> **포인트 기반 선착순 구매 시스템**  
> 대용량 트래픽과 동시성 문제 해결에 중점을 둔 **MSA 기반** 포트폴리오 프로젝트

<br/>

---
## 프로젝트 개요

| 항목 | 내용 |
|------|------|
| 프로젝트명 | Project Avalanche |
| 핵심 기능 | 선착순 구매, 동시성 제어, 분산 트랜잭션 |
| 주요 기술 | Spring Boot, Redis, Kafka, Docker, Saga |
| MSA 서비스 수 | 6개 (Auth, User, Order, Product, Point, Shipping) |


### ❄️ 핵심 목표
- **MSA(Microservices Architecture)** 기반의 확장 가능한 시스템 설계
- **대용량 트래픽** 환경에서의 동시성 제어 및 성능 최적화
- **Redis**를 활용한 캐싱 전략으로 **선착순 구매** 기능 구현
- **분산 트랜잭션** 처리를 위한 Saga 패턴 적용

### ❄️ 핵심 기능
- **실시간 재고 관리**: Redis 분산 락을 통한 동시성 제어
- **분산 트랜잭션**: Saga 패턴을 통한 안전한 주문 처리
- **API Gateway**: 모든 요청의 중앙 집중 라우팅
- **재고 예약 시스템**: 결제 진행 중 재고 임시 점유로 동시 구매 방지
- **멀티 디바이스 관리**: 클라이언트 ID 기반 개별/전체 기기 로그아웃 기능
- **이메일 인증**: SMTP 기반 30분 만료 토큰으로 회원가입 검증
- **배치 동기화**: Redis 예약 재고를 30초 간격으로 PostgreSQL 메인 DB에 반영

### ❄️ 기술적 도전 과제
- **동시성 문제**: Redis 분산 락으로 Race Condition 방지
- **데이터 일관성**: Saga 패턴을 통한 분산 트랜잭션 처리
- **성능 최적화**: Redis 캐싱으로 DB 부하 감소
- **장애 복구**: 실패 시 자동 보상 트랜잭션 구현

### ❄️ 기술 스택

> Backend

- **Language** `Java 21`
- **Framework** `Spring Boot 3.2.2` `Spring Security` `Spring Cloud`
- **Build Tool** `Gradle`
- **Authentication** `JWT`

> Database & Cache

- **Main DB** `PostgreSQL`
- **Cache** `Redis` `Redisson-분산 락`
- **TTL 정책** `Saga 상태-60초` `재고 캐시-300초`

> Message Queue & Communication

- **Message Broker** `Apache Kafka`
- **Service Discover** `Eureka`

> DevOps & Infrastructure

- **Containerization** `Docker` `Docker Compose`

<br/>

---
## 데이터베이스 설계


![erd.png](docs%2Fimages%2Ferd.png)


<br/>

---
## 시스템 아키텍처


### ❄️ MSA 구성도

![msa.png](docs%2Fimages%2Fmsa.png)


<br/>

---
## 서비스 구성


| 서비스 | 주요 책임 | 핵심 기술                                      |
|--------|-----------|--------------------------------------------|
| **Auth Service** | 인증/인가, JWT 토큰 관리 | Spring Security, Redis                     |
| **User Service** | 사용자 관리, 프로필 | JPA, PostgreSQL                            |
| **Order Service** | 주문 처리, Saga 오케스트레이션 | Kafka, Redisson                            |
| **Product Service** | 상품/재고 관리, 동시성 제어 |  Redis 분산락, QueryDSL, Kafka, 보상 트랜잭션 |
| **Point Service** | 포인트 차감 | Kafka, 보상 트랜잭션                             |
| **Shipping Service** | 배송 관리 | Kafka, 보상 트랜잭션                             |

<br/>

---
## 분산 트랜잭션 처리


### ❄️ 선착순 구매 프로세스

1. **상품 조회**: Redis 캐시에서 실시간 재고 확인
2. **구매 시도**: 분산 락 획득 후 예약 재고 차감
3. **확률 계산**: 상품별 등급에 따른 당첨 확률 적용
4. **결제 처리**: Saga 패턴으로 포인트 차감 및 주문 생성
5. **재고 확정**: 성공 시 실제 재고 차감, 실패 시 예약 재고 복원

**정상 플로우**

![happypath.png](docs%2Fimages%2Fhappypath.png)

**보상 트랜잭션**

![compensation.png](docs%2Fimages%2Fcompensation.png)

#### 주문 실패 시나리오 예시

```json
{
  "status": "CANCELLED",           // 4. 주문 취소 처리
  "commandStatuses": {
    "CHECK_STOCK": "SUCCESS",      // 1. 예약 재고 차감 성공
    "APPLY_POINTS": "FAILED",      // 2. 포인트 차감 실패
    "PREPARE_SHIPPING": "SUCCESS"  // 1. 주소지 등록 성공
  },
  "compensationStatuses": {
    "CHECK_STOCK": "SUCCESS",      // 3. 재고 복원 완료
    "PREPARE_SHIPPING": "SUCCESS"  // 3. 배송 준비 취소 완료
  }
}
```

<br/>

---
## 기술적 성과 및 학습


### ❄️ 해결한 기술적 문제들

**분산 트랜잭션**

- 문제: MSA 환경에서 여러 서비스에 걸친 데이터 일관성
- 해결: Choreography 방식의 Saga 패턴 적용
- 결과: 서비스 실패 시 자동 보상 트랜잭션으로 일관성 보장


**동시성 제어**

- 문제: 동일 상품에 대한 동시 주문 요청 시 재고 부정확성
- 해결: Redisson 분산 락으로 원자적 재고 처리
- 결과: Race Condition 방지 및 정확한 재고 관리
- 

**성능 최적화**

- 캐싱 전략: Redis를 통한 상품/재고 정보 캐싱
- 비동기 처리: CompletableFuture를 활용한 논블로킹 주문 처리
- 폴링 최적화: 1초 간격으로 최대 30초간 주문 상태 확인


<br/>

---
## 프로젝트 회고


### ❄️ 성장한 부분
- **MSA 설계 역량**: 서비스 간 책임 분리와 통신 방식 설계
- **동시성 제어**: 분산 락을 통한 Race Condition 해결
- **분산 트랜잭션**: Saga 패턴으로 데이터 일관성 보장

### ❄️ 아쉬운 부분
- **부하 테스트**: 시나리오 작성 및 요청 처리 흐름은 구현했으며, 부하 테스트 결과 정제 및 시각화는 향후 보완 예정
- **모니터링**: Grafana 등 모니터링 도구 학습 필요
- **성능 최적화**: 대용량 테스트 부족으로 실제 병목점 식별 불가

### ❄️ 향후 계획
- [ ] JMeter 시나리오 작성 및 튜닝 역량 강화로 목표 부하 테스트 완성
- [ ] 테스트 결과 기반 성능 병목 지점 개선
- [ ] ELK 스택 도입으로 로그 중앙화 및 실시간 모니터링
