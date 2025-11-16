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
                    "부모님은 언제나 제가 하고 싶은 일을 존중해주셨다. 어린 마음에 엉뚱한 관심사가 생겨도, 그저 미소 지으며 지켜봐 주셨고, 때로는 함께 도와주기도 했다. 하지만 잘못된 길로 가려 할 때는 단호하게 잡아주시는 분들이었다. 그 단호함이 어린 시절엔 부담스럽게 느껴질 때도 있었지만, 지금 돌이켜보면 그 모든 행동이 저를 지키기 위한 사랑이었음을 알게 된다. 부모님의 이런 모습은 나에게 “사랑은 늘 부드럽기만 한 것이 아니라, 때로는 바르게 걷게 만드는 힘이기도 하다”는 사실을 가르쳐주었다.", "", "", ""),
            AllAutobiographyItemModel(1, 0, 0, "자서전2", "", "", "", ""),
            AllAutobiographyItemModel(2, 0, 0, "자서전3", "", "", "", "")
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