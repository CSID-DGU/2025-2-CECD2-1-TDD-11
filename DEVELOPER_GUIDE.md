# GitHub 협업 가이드

## 모노레포 협업 방식 (현업 스타일)

### 프로젝트 구조
```
├── app/        # 모바일 앱
├── web/        # 웹 프론트엔드  
├── server/     # 백엔드 서버
├── ai/         # AI 모델
└── ...
```

### 브랜치 전략
- **main**: 배포용 브랜치 (직접 수정 금지 ⚠️)
- **{domain}/dev**: 각 도메인별 개발 브랜치 (app/dev, web/dev, server/dev, ai/dev)
- **{domain}/prod**: 각 도메인별 배포 준비 브랜치 (app/prod, web/prod, server/prod, ai/prod)

### 작업 흐름
1. **본인 담당 도메인의 dev 브랜치에서 작업**
   ```bash
   git checkout app/dev    # 앱 개발자
   git checkout web/dev    # 웹 개발자
   git checkout server/dev # 백엔드 개발자
   git checkout ai/dev     # AI 개발자
   ```

2. **작업 완료 후 해당 도메인의 prod 브랜치로 PR**
   - `app/dev` → `app/prod`
   - `web/dev` → `web/prod`
   - `server/dev` → `server/prod`
   - `ai/dev` → `ai/prod`

3. **prod 브랜치 머지 시 자동으로 main 브랜치에 반영**
   - GitHub Actions가 자동으로 `{domain}/prod` → `main` 머지 수행

### ⚠️ 중요 규칙
- **절대 main 브랜치를 직접 수정하지 마세요**
- **다른 도메인의 브랜치에서 작업하지 마세요**
- **본인 담당 도메인의 dev → prod 흐름만 사용하세요**

---

## 1. 프로젝트 시작하기 (택 1)

### 1.1. Fork 방식
1. 메인 리포지토리를 본인 계정으로 Fork
2. Fork한 리포지토리를 로컬에 클론
```bash
git clone https://github.com/본인계정/part-time-worker.git
cd part-time-worker
```

3. 원본 리포지토리를 upstream으로 추가
```bash
git remote add upstream https://github.com/원본계정/part-time-worker.git
```

### 1.2. 브랜치 방식
1. 메인 리포지토리를 직접 클론
```bash
git clone https://github.com/팀계정/part-time-worker.git
cd part-time-worker
```

2. 브랜치 생성 (하단에서 서술)

## 2. 작업 시작하기

### 최신 코드 받기
```bash
# Fork 방식
git fetch upstream
git checkout main
git merge upstream/main

# 브랜치 방식
git pull origin main
```

### 새 브랜치 생성 (브랜치 방식만 필수, fork는 선택)
```bash
# 기본 브랜치 생성
git checkout -b 브랜치명
# 예: git checkout -b feature/player-jump

# 이슈 번호도 함께 보여주고 싶은 경우
git checkout -b 타입/이슈번호-기능명
# 예: git checkout -b feature/123-player-jump
```

## 3. 작업하고 커밋하기

### 파일 수정 후 커밋
```bash
# 기본 커밋 (add . : 전체 파일 추가 명령어)
git add .
git commit -m "feat: 플레이어 점프 기능 추가"

# 이슈에 함께 커밋하고 싶은 경우
git add .
git commit -m "feat: 플레이어 점프 기능 추가 #이슈번호"
```

### 브랜치에 푸시
```bash
# Fork 방식
git push origin 브랜치명

# 브랜치 방식 최초 commit
git push --set-upstream origin 브랜치명

# 브랜치 방식 기본 commit
git push origin 브랜치명
```

## 4. Pull Request 생성

1. GitHub 웹사이트에서 본인 리포지토리(Fork) 또는 메인 리포지토리로 이동
2. "Compare & pull request" 버튼 클릭
3. PR 템플릿에 따라 내용 작성
4. "Create pull request" 클릭

## 5. 코드 리뷰 및 머지

### 리뷰어가 할 일
1. PR 페이지에서 "Files changed" 탭 클릭
2. 코드 검토 후 "Review changes" 버튼 클릭
3. 리뷰 타입 선택:
   - **Approve**: 코드가 좋음, 머지 승인
   - **Request changes**: 수정 필요
   - **Comment**: 단순 의견 제시
4. 코멘트 작성 후 "Submit review" 클릭

### PR 작성자가 할 일
1. **2명 이상의 Approve**를 받을 때까지 대기
2. 수정 요청이 있으면 같은 브랜치에서 추가 커밋
3. 2명 이상 승인되면 **본인이 직접 머지**
4. "Merge pull request" → "Confirm merge" 클릭
5. 머지 후 브랜치 삭제 ("Delete branch" 클릭)

## 6. 작업 완료 후 정리

### 원격 브랜치 정리 (권장)
```bash
# main 브랜치로 이동
git checkout main

# 머지된 원격 브랜치 삭제
git push origin --delete 브랜치명

# 혹은 github gui에서 직접 수행
```
**권장 사유**: 원격에 브랜치가 많이 쌓이면 복잡해짐

### 최신 코드로 업데이트
```bash
# Fork 방식
git fetch upstream
git merge upstream/main

# 브랜치 방식
git pull origin main
```

## 브랜치 명명 규칙 (추천)

- `feature/기능명`: 새로운 기능
- `fix/버그명`: 버그 수정
- `refactor/대상`: 리팩토링
- `docs/문서명`: 문서 수정

예시:
- `feature/player-movement`
- `fix/enemy-collision`
- `refactor/game-manager`

## 주의사항

1. **절대 main 브랜치에서 직접 작업하지 마세요**
2. **커밋 전에 항상 테스트하세요**
3. **PR 생성 전에 최신 코드와 충돌 확인하세요**
4. **의미 있는 커밋 메시지를 작성하세요**

## 문제 해결

### 충돌(Conflict) 발생 시
주로 아래와 같은 상황에 발생합니다.
- 내 브랜치에서 작업 내용 PR -> Merge 받은 후, 다시 main 브랜치 (상위 브랜치) 의 내용을 pull로 업데이트 하지 않았을 때
- 어느 순간 --force push를 사용한 경우
- 어느 순간 rebase를 사용한 경우
- 여러 명이 같은 브랜치에서 작업한 경우
- 원격 브랜치로 올라간 커밋을 이전으로 되돌린 후 다시 push로 업로드 하려는 경우

**해결 방법:** github remote commit과 local 작업 환경 commit 사이 history가 달라서 발생하는 것으로, history 복구가 가능하다면 복구 후 재시도
- 너무 어렵다면 도움 요청..!!

### 실수로 main에서 작업한 경우
```bash
# 새 브랜치 생성하고 변경사항 이동
git checkout -b 브랜치명
git add .
git commit -m "커밋 메시지"
git push origin 브랜치명
```
