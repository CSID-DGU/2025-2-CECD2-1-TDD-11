package com.tdd.talktobook.core.designsystem

// Common
const val BookShelf = "대화로책"
const val Empty = ""
const val Blank = " "
const val Confirm = "확인"
const val Next = "다음"
const val NextTime = "나중에"
const val GoToHome = "홈으로"
const val ServerErrorToast = "네트워크 오류가 발생했습니다."

// Day
const val Mon = "월"
const val Tue = "화"
const val Wed = "수"
const val Thu = "목"
const val Fri = "금"
const val Sat = "토"
const val Sun = "일"
const val DateContent = "%d일"

// Publish Status
const val Requested = "REQUESTED"
const val RequestConfirmed = "REQUEST_CONFIRMED"
const val InPublishing = "IN_PUBLISHING"
const val Published = "PUBLISHED"
const val Rejected = "REJECTED"
const val NotPublished = "NOT_PUBLISHED"

// Autobiography Status
const val EmptyStatus = "EMPTY"
const val ProgressingStatus = "PROGRESSING"
const val EnoughStatus = "ENOUGH"
const val CreatingStatus = "CREATING"
const val FinishStatus = "FINISH"

// Interview Conversation Status
const val BeforeStart = "진행 전"
const val InterviewIng = "진행 중"
const val FinishRequest = "다음 질문 요청 가능"

// Book Title
const val Top = "TOP"
const val Middle = "MID"
const val Bottom = "BOTTOM"
const val Left = "LEFT"

// Bottom Navigation
const val Home = "홈"
const val Publication = "자서전"
const val Interview = "인터뷰"

// Gender
const val Male = "MALE"
const val Female = "FEMALE"
const val MaleContent = "남자"
const val FemaleContent = "여자"

// Age
const val Ten = "1020"
const val Thirty = "30"
const val Forty = "40"
const val Fifty = "50"
const val Sixty = "60"
const val Seventy = "70"
const val TenContent = "10대/20대"
const val ThirtyContent = "30대"
const val FortyContent = "40대"
const val FiftyContent = "50대"
const val SixtyContent = "60대"
const val SeventyContent = "70대"

// Material
const val Family = "family"
const val Love = "love"
const val Caring = "caring"
const val Local = "local"
const val Trait = "trait"
const val Friend = "friend"
const val Career = "career"
const val Growing = "growing"
const val Crisis = "crisis"
const val Money = "money"
const val Hobby = "hobby"
const val Pet = "pet"
const val Philosophy = "philosophy"
const val Community = "community"
const val Parent = "parent"
const val FamilyContent = "가족사 전반"
const val LoveContent = "사랑과 결혼"
const val CaringContent = "돌봄과 양육"
const val LocalContent = "집과 동네의 기억"
const val TraitContent = "나의 성격과 습관"
const val FriendContent = "관계망과 우정"
const val FriendCoShowContent = "인간관계"
const val CareerContent = "커리어 여정"
const val CareerCoShowContent = "일과 커리어"
const val GrowingContent = "성장의 여정"
const val GrowingCoShowContent = "어린 시절"
const val CrisisContent = "위기,회복,성찰"
const val MoneyContent = "돈과 선택"
const val HobbyContent = "취미,여가,정체성"
const val PetContent = "반려동물과 함께한 삶"
const val PetCoShowContent = "반려동물"
const val PhilosophyContent = "가치관과 철학"
const val CommunityContent = "공동체와 일상"
const val ParentContent = "부모 이야기 집중"

// LogIn
const val LogInText = "로그인"
const val EmailHintText = "이메일을 입력해 주세요."
const val PasswordHintText = "비밀번호를 입력해 주세요."
const val StartWithoutLogIn = "체험하기"
const val LogInCheckEmailType = "이메일 형식이 올바른지 확인해주세요"
const val LogInWrongEmailPW = "이메일 혹은 비밀번호가 일치하지 않습니다"
const val LogInNoExistMember = "존재하지 않은 회원입니다."
const val LogInDeleteUser = "이미 탈퇴한 회원입니다."

// SignUp
const val SignUpText = "회원가입"
const val SignUpMemberExistAlready = "이미 존재하는 회원입니다. 바로 로그인하세요."
const val SignUpEmailError = "올바른 이메일 형식을 입력해주세요"
const val SignUpPassWordError = "비밀번호는 8자 이상, 대문자/소문자/숫자/특수문자를 포함해야 합니다.\n연속된 동일 문자는 3회 이내만 가능합니다."

// Email Check
const val EmailCheckText = "이메일 인증"
const val CodeHintText = "인증코드를 입력해 주세요."

// Change Password
const val ChangePasswordText = "비밀번호 재설정"

// OnBoarding
const val OnBoardingFirstPage = "연령대를 선택해 주세요."
const val OnBoardingSecondPage = "성별을 선택해 주세요."
const val OnBoardingThirdPage = "직업군을 작성해 주세요."
const val OccupationWriteHint = "직업군을 작성해 주세요. ex) 개발자"

// Home
const val HomeTitle = "대화에서 발견한 소중한 기억들"
const val HomeProgressTitle = "대화 진행률"
const val HomeProgressFinish = "자서전 완성까지 "
const val HomeStartProgress = "자서전 생성 시작하기"
const val HomeNotExistInterview = "대화 내역 없음"
const val HomeNotExistSummary = "대화 내역이 없습니다"

// Start Progress
const val StartProgressTitle = "자서전 생성 시작"
const val StartProgressBeginPage = "자서전을 생성할 이름(닉네임)을 작성해 주세요."
const val NickNameInputHint = "닉네임 (ex: 대화로)"
const val StartProgressFirstPage = "가장 처음 대화하고 싶은 주제를\n선택해 주세요."
const val StartProgressSecondPage = "자서전의 생성 목적을\n간단히 작성해 주세요."
const val StartInterviewBtn = "인터뷰 시작"
const val ReasonWriteHint = "자서전을 생성하고자 하는 이유를\n간단히 작성해 주세요."
const val ReasonWriteTextMax = "%d/300"

// Interview
const val InterviewScreenTitle = "인터뷰"
const val InterviewStartBtn = "답변 시작하기"
const val InterviewFinishBtn = "답변 중단하기"
const val InterviewContinuous = "이어 말하기"
const val InterviewReAnswer = "다시 말하기"
const val InterviewNextQuestion = "다음 질문 넘어가기"

// Dialog
const val StartAutobiographyDialogTitle = "자서전 생성 시작하기"
const val StartAutobiographyDialogContent = "지금 바로 대화 인터뷰를 통해\n나만의 자서전을 생성해 보세요!"
const val StartAutobiographyDialogBtn = "자서전 생성 시작하기"
const val CreateAutobiographyDialogTitle = "자서전 생성"
const val CreateAutobiographyDialogContent = "자서전 생성이 가능합니다.\n인생 이야기를 확인해 보세요!"
const val CreateAutobiographyDialogBtn = "자서전 생성 요청"
const val SkipQuestionTitle = "해당 질문 넘기기"
const val SkipQuestionContent = "질문을 넘기고 싶은 이유를 선택해 주세요"
const val SkipQuestionFirstBtn = "이전에 답변한 질문과 비슷해요"
const val SkipQuestionSecondBtn = "뭐라고 답변해야 할 지 모르겠어요"
const val SkipQuestionBottomHint = "기타 이유"
const val SkipQuestionReason = "다른 얘기 하고 싶어"
const val RequestSuccessInCoShowFlow = "자서전을 생성 요청했어요!"

// Publication
const val PublicationTitle = "생성된 자서전"
const val PublicationBookWholeContent = "전체 보기"
const val PublicationBookDelete = "자서전 삭제하기"
const val PublicationNotCreatedAutobiography = "아직 생성된 자서전이 없어요!"

// Autobiography Request
const val DownLoadPdf = "자서전 다운받기"
const val AutobiographyPdfName = "대화로책 자서전.pdf"

// Setting
const val SettingTitle = "프로필"
const val SettingProfileEdit = "수정"
const val SettingEmail = "이메일: "
const val SettingAge = "연령대: "
const val SettingGender = "성별: "
const val SettingOccupation = "직업군: "
const val SettingAlarm = "알림"
const val SettingPolicy = "개인정보 처리방침"
const val SettingService = "서비스 이용 약관"
const val SettingUserFeedback = "사용자 피드백"
const val SettingCurrentVersion = "현재 버전 "
const val SettingLogOut = "로그아웃"
const val SettingDelete = "회원 탈퇴"
