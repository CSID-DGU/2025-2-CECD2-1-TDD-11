package com.tdd.bookshelf.feature.publication

import com.tdd.bookshelf.core.ui.base.BaseViewModel
import com.tdd.bookshelf.domain.entity.response.autobiography.AllAutobiographyItemModel
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class PublicationViewModel(): BaseViewModel<PublicationPageState>(
    PublicationPageState()
) {
    init {
        initSetAutobiographyList()
    }

    private fun initSetAutobiographyList() {
        val autobiographies: List<AllAutobiographyItemModel> = listOf(
            AllAutobiographyItemModel(0, 0, 0, "자서전1", "어릴 적 우리 집은 늘 시끌벅적했다. 아침마다 식탁 위에서는 토스트 굽는 냄새와 따끈한 국물 냄새가 함께 풍겨왔고, 서로 학교 갈 준비를 하느라 복도에서는 발걸음 소리가 끊이질 않았다. 그 와중에도 어머니는 잊지 않고 “밥은 꼭 먹고 가라”고 외치셨고, 아버지는 “오늘도 좋은 하루 보내라”며 늘 같은 미소로 우리를 배웅하셨다. 그렇게 정신없이 바쁘게 움직이면서도, 꼭 한 번은 누군가의 실수나 말 한마디 때문에 온 가족이 동시에 웃음이 터져 나오는 집이었다. 바람처럼 스쳐 지나가는 아침 시간이었지만, 그 안에는 우리 가족만의 따뜻함이 담겨 있었다.\n" +
                    "\n" +
                    "부모님은 언제나 제가 하고 싶은 일을 존중해주셨다. 어린 마음에 엉뚱한 관심사가 생겨도, 그저 미소 지으며 지켜봐 주셨고, 때로는 함께 도와주기도 했다. 하지만 잘못된 길로 가려 할 때는 단호하게 잡아주시는 분들이었다. 그 단호함이 어린 시절엔 부담스럽게 느껴질 때도 있었지만, 지금 돌이켜보면 그 모든 행동이 저를 지키기 위한 사랑이었음을 알게 된다. 부모님의 이런 모습은 나에게 “사랑은 늘 부드럽기만 한 것이 아니라, 때로는 바르게 걷게 만드는 힘이기도 하다”는 사실을 가르쳐주었다.\n" +
                    "서로에게 서툴러서 마음을 표현하지 못했던 순간들도 많았다. 사춘기 시절엔 괜히 날카롭게 굴며 가족의 걱정을 밀어내기도 했고, 부모님의 기대와 내 마음 사이에서 혼란을 겪기도 했다. 하지만 시간이 흐를수록 부모님이 보여주신 작은 행동들—출근길에 제 책상을 한번 더 정돈해두시던 모습, 늦게 귀가하면 불 꺼진 거실에서 조용히 기다리고 계시던 모습, 아플 때는 말없이 따뜻한 물수건을 건네주시던 모습—이 모두 커다란 사랑이었다는 사실을 깨닫게 되었다. 말로 표현하지 않아도 전해지는 사랑이 있다는 것을 나는 가족을 통해 배웠다. 우리 가족은 겉으로 보기엔 특별한 이야기가 있는 건 아닐지도 모른다. 유명한 집안도 아니고, 드라마 같은 사건이 있었던 것도 아니다. 하지만 제가 살아오면서 수많은 순간에 힘을 낼 수 있었던 이유는 언제나 가족이었다. 기쁜 날엔 누구보다 먼저 축하해주고, 힘든 날엔 아무 말 없이 옆에서 손을 잡아주며 함께 울어주는 사람들이었다. 그들이 곁에 있다는 사실 하나만으로도 세상의 어려움이 조금은 덜 무겁게 느껴졌다.", "", "", ""),
            AllAutobiographyItemModel(1, 0, 0, "자서전2", "자서전2 내용", "", "", ""),
        )

        updateState(
            uiState.value.copy(
                autobiographyList = autobiographies,
                selectedAutobiographyId = autobiographies[0].autobiographyId
            )
        )
    }

    fun setSelectedAutobiographyId(id: Int) {
        updateState(
            uiState.value.copy(
                selectedAutobiographyId = id
            )
        )
    }
}