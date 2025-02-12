package com.guenbon.siso.util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomNicknameGenerator {
    private RandomNicknameGenerator() {

    }

    private static final List<String> ADJECTIVES = Arrays.asList(
            "귀여운", "멋진", "행복한", "슬픈", "즐거운", "용감한", "현명한", "다정한", "화난", "차가운",
            "따뜻한", "환한", "어두운", "느린", "빠른", "달콤한", "쓴", "신", "짜릿한", "부드러운",
            "강한", "약한", "튼튼한", "유연한", "무거운", "가벼운", "조용한", "시끄러운", "평온한", "불안한",
            "순한", "거친", "깊은", "얕은", "밝은", "어두운", "화려한", "소박한", "단순한", "복잡한",
            "예쁜", "못생긴", "젊은", "늙은", "새로운", "오래된", "깨끗한", "더러운", "화난", "기쁜",
            "건조한", "축축한", "뾰족한", "둥근", "평평한", "울퉁불퉁한", "부드러운", "거친", "무서운", "귀여운",
            "따뜻한", "차가운", "활기찬", "졸린", "풍요로운", "가난한", "예민한", "둔한", "무딘", "예리한",
            "가득한", "빈", "단단한", "말랑한", "굳은", "흐린", "맑은", "은은한", "화려한", "청량한",
            "화사한", "강렬한", "잔잔한", "부드러운", "거친", "자유로운", "속박된", "사랑스러운", "미운", "싱그러운",
            "거룩한", "불경한", "신비로운", "익숙한", "낯선", "용감한", "겁쟁이", "유쾌한", "우울한", "희망찬"
    );

    private static final List<String> COLORS = Arrays.asList(
            "빨간색", "파란색", "노란색", "초록색", "보라색", "주황색", "갈색", "회색", "검은색", "흰색",
            "분홍색", "청록색", "남색", "하늘색", "연두색", "자주색", "황금색", "은색", "진홍색", "연보라색"
    );

    private static final List<String> ANIMALS = Arrays.asList(
            "강아지", "고양이", "말", "호랑이", "사자", "코끼리", "기린", "곰", "여우", "늑대",
            "다람쥐", "토끼", "쥐", "돼지", "양", "염소", "닭", "오리", "독수리", "참새",
            "비둘기", "까치", "고래", "상어", "돌고래", "문어", "오징어", "게", "새우", "거북이",
            "도마뱀", "카멜레온", "악어", "뱀", "펭귄", "두더지", "코알라", "캥거루", "판다", "수달",
            "너구리", "담비", "밍크", "재규어", "퓨마", "치타", "스컹크", "고슴도치", "개미핥기", "하이에나",
            "바다표범", "물개", "바다사자", "알파카", "라마", "미어캣", "사슴", "노루", "말코손바닥사슴", "순록",
            "북극곰", "스라소니", "늑대개", "퓨마", "갈매기", "앵무새", "공작", "벌새", "까마귀", "매",
            "수리", "홍학", "고라니", "사향고양이", "담비", "족제비", "하마", "코뿔소", "두루미", "산양",
            "해달", "돌쥐", "자라", "삵", "까투리", "멧돼지", "여우원숭이", "바다코끼리", "오소리", "들개",
            "라쿤", "박쥐", "거미", "전갈", "달팽이", "장수풍뎅이", "사마귀", "개미", "벌", "잠자리"
    );

    private static final Random RANDOM = new Random();

    public static String generateNickname() {
        String adjective = ADJECTIVES.get(RANDOM.nextInt(ADJECTIVES.size()));
        String color = COLORS.get(RANDOM.nextInt(COLORS.size()));
        String animal = ANIMALS.get(RANDOM.nextInt(ANIMALS.size()));
        return adjective + " " + color + " " + animal;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(generateNickname());
        }
    }
}
