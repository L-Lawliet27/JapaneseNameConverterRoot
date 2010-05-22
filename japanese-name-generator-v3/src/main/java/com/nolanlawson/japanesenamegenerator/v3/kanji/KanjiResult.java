package com.nolanlawson.japanesenamegenerator.v3.kanji;

/**
 *
 * @author nolan
 */
public class KanjiResult {

    private String kanji;
    private String roomaji;
    private String english;

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getKanji() {
        return kanji;
    }

    public void setKanji(String kanji) {
        this.kanji = kanji;
    }

    public String getRoomaji() {
        return roomaji;
    }

    public void setRoomaji(String roomaji) {
        this.roomaji = roomaji;
    }

    public String toString() {
        return "<"+kanji+","+roomaji+"," + english +">";
    }
    
    public KanjiResult clone() {
    	KanjiResult clone = new KanjiResult();
    	clone.setEnglish(english);
    	clone.setKanji(kanji);
    	clone.setRoomaji(roomaji);
    	return clone;
    }

}