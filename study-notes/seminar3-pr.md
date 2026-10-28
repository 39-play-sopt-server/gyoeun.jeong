# PR 제목

feat: 게시글 저장소를 MySQL·JPA로 전환하고 수정 트랜잭션 적용

---

## 👷 구현 내용

### 1. Repository 전환

- Map에 직접 저장·조회하던 클래스를 `JpaRepository<Post, Long>` 상속 인터페이스로 변경
- 메모리 저장용 Map과 CRUD 메서드 구현 제거
- 카테고리별 Stream 필터링을 `findByCategory()` 쿼리 메서드로 전환
- Spring Data JPA가 제공하는 Repository 구현을 통한 DB 접근 방식 적용

### 2. Service와 트랜잭션

- 게시글 생성 시 `repository.add(post)`를 `repository.save(post)`로 변경
- Service의 `nextId++` 제거 및 DB의 ID 자동 생성 방식 사용
- 게시글 수정 메서드 `updatePost()`에 `@Transactional` 적용
- 조회수 증가 메서드 `viewPost()`에 `@Transactional` 적용
- 조회와 객체 변경을 하나의 쓰기 트랜잭션으로 묶고 더티 체킹으로 DB에 반영하는 구조 적용

## 💭 구현하며 고민한 내용

### 1. MySQL과 JPA의 역할 및 Repository 자동 구현

- 기존 ID 생성 주체는 Service의 `nextId++`, Map의 역할은 생성된 ID를 키로 게시글 보관
- 변경 후 `IDENTITY` 설정에 따른 MySQL의 ID 자동 생성
- `@Entity`의 매핑 정보를 이용한 JPA의 DB 데이터와 `Post` 객체 간 변환
- `JpaRepository<Post, Long>` 상속을 통한 기본 저장·조회·삭제 기능 사용
- Spring Data JPA의 메서드 이름 해석을 통한 `findByCategory()` 조건 조회 처리
- 클래스에서 인터페이스로 변경하면서 기존 Map과 메서드 구현을 함께 제거할 필요성 확인

### 2. 영속성 컨텍스트와 트랜잭션 적용 이유

- 영속성 컨텍스트는 JPA가 엔티티를 관리하는 공간, 영속은 관리 중인 상태, 준영속은 관리에서 벗어난 상태
- `post.update(...)` 자체의 역할은 자바 객체의 필드 값 변경
- 더티 체킹의 핵심 조건은 메서드의 제공 주체가 아닌 관리 중인 객체의 변경 여부
- Repository 기본 메서드의 트랜잭션만으로 Service의 조회와 변경 전체를 묶는 데 한계
- `@Transactional`로 Spring에 Service 메서드 전체의 트랜잭션 처리를 요청하고, 조회부터 변경까지 하나의 쓰기 트랜잭션으로 구성
- 조회 직후 무조건 준영속으로 전환된다는 이해 수정 및 실제 관리 상태는 영속성 컨텍스트의 수명·설정에 따른 차이 존재

### 3. save()와 더티 체킹의 차이

- 새 게시글 생성 시 `save()` 사용
- 쓰기 트랜잭션 안에서 조회한 객체 수정 시 더티 체킹 적용 및 추가 `save()` 생략 가능
- 준영속 객체도 `save()`의 `merge()`를 통해 변경 내용을 DB에 반영 가능
- `merge()`는 준영속 객체를 만드는 과정이 아닌, 전달받은 객체의 값을 관리 중인 객체에 복사하는 과정
- 준영속 객체 병합 시 원래 객체와 `merge()`가 반환하는 관리 객체의 구분 필요
- `save()`를 잘못된 수정 방식으로 보던 이해 수정 및 이번 실습에서는 조회·검증·변경을 묶는 더티 체킹 방식 선택

### 4. 더티 체킹·flush·commit의 처리 순서

1. **조회와 변경:** 트랜잭션 안에서 게시글을 조회한 뒤 객체의 값 변경
2. **더티 체킹:** JPA가 관리 중인 객체의 변경 여부 감지
3. **flush:** 변경 내용을 수정 SQL로 DB에 반영
4. **commit:** 트랜잭션의 DB 변경 최종 확정

- flush와 commit의 구분 및 flush 이후에도 롤백 시 해당 트랜잭션의 DB 변경 취소 가능
- 보통 커밋 전 자동 flush 발생 및 쿼리·설정에 따른 조기 flush 가능
- 트랜잭션 실패 시 예외 종류와 설정에 따른 롤백 처리 및 현재 사용자 정의 `RuntimeException`의 기본 롤백 대상 해당

## ✅ 검증 현황

PR 초안 작성 과정에서 코드·커밋 차이 확인 완료, 빌드·서버 실행·DB 연결 검증 미수행

- [ ] 컴파일 및 MySQL 연결 확인
- [ ] 게시글 생성·전체 조회·카테고리별 조회·삭제 확인
- [ ] 게시글 수정과 조회수 증가의 DB 반영 확인
- [ ] 서버 재시작 후 데이터 유지 확인

## 🚨 추가 검토 사항

- Repository의 불필요한 기본 메서드 선언과 관련 import 제거
- 중복 JUnit 의존성과 `PostCategory`의 미사용 필드·생성자 정리
- 카테고리의 문자열 매핑 및 삭제 기능의 Service 트랜잭션 검토
- 조회수 증가에 대한 동시성 제어 별도 검토

## 참고 자료

- [3차 세미나 자료 — 실습](https://app.notion.com/p/sopt-official/3-3f81e48dd96080da9ebacc5e1e051e5c?source=copy_link)
- [Spring Data JPA — 엔티티 저장](https://docs.spring.io/spring-data/jpa/reference/jpa/entity-persistence.html)
- [Spring Data JPA — 트랜잭션 경계](https://docs.spring.io/spring-data/jpa/reference/jpa/transactions.html)
- [Jakarta Persistence — 엔티티 상태·merge·flush](https://jakarta.ee/specifications/persistence/3.2/apidocs/jakarta.persistence/jakarta/persistence/EntityManager.html)
