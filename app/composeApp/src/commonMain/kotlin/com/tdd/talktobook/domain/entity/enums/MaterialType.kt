package com.tdd.talktobook.domain.entity.enums

import com.tdd.talktobook.core.designsystem.Career
import com.tdd.talktobook.core.designsystem.CareerContent
import com.tdd.talktobook.core.designsystem.Caring
import com.tdd.talktobook.core.designsystem.CaringContent
import com.tdd.talktobook.core.designsystem.Community
import com.tdd.talktobook.core.designsystem.CommunityContent
import com.tdd.talktobook.core.designsystem.Crisis
import com.tdd.talktobook.core.designsystem.CrisisContent
import com.tdd.talktobook.core.designsystem.Empty
import com.tdd.talktobook.core.designsystem.Family
import com.tdd.talktobook.core.designsystem.FamilyContent
import com.tdd.talktobook.core.designsystem.Friend
import com.tdd.talktobook.core.designsystem.FriendContent
import com.tdd.talktobook.core.designsystem.Growing
import com.tdd.talktobook.core.designsystem.GrowingContent
import com.tdd.talktobook.core.designsystem.Hobby
import com.tdd.talktobook.core.designsystem.HobbyContent
import com.tdd.talktobook.core.designsystem.Local
import com.tdd.talktobook.core.designsystem.LocalContent
import com.tdd.talktobook.core.designsystem.Love
import com.tdd.talktobook.core.designsystem.LoveContent
import com.tdd.talktobook.core.designsystem.Money
import com.tdd.talktobook.core.designsystem.MoneyContent
import com.tdd.talktobook.core.designsystem.Parent
import com.tdd.talktobook.core.designsystem.ParentContent
import com.tdd.talktobook.core.designsystem.Pet
import com.tdd.talktobook.core.designsystem.PetContent
import com.tdd.talktobook.core.designsystem.Philosophy
import com.tdd.talktobook.core.designsystem.PhilosophyContent
import com.tdd.talktobook.core.designsystem.Trait
import com.tdd.talktobook.core.designsystem.TraitContent

enum class MaterialType(
    val type: String,
    val content: String,
) {
    FAMILY(Family, FamilyContent),
    LOVE(Love, LoveContent),
    CARING(Caring, CaringContent),
    LOCAL(Local, LocalContent),
    TRAIT(Trait, TraitContent),
    FRIEND(Friend, FriendContent),
    CAREER(Career, CareerContent),
    GROWING(Growing, GrowingContent),
    CRISIS(Crisis, CrisisContent),
    MONEY(Money, MoneyContent),
    HOBBY(Hobby, HobbyContent),
    PET(Pet, PetContent),
    PHILOSOPHY(Philosophy, PhilosophyContent),
    COMMUNITY(Community, CommunityContent),
    PARENT(Parent, ParentContent),
    DEFAULT(Empty, Empty);
}