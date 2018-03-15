package com.naren.kagga.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Kagga implements Parcelable{

	private String kagga;
	private String dividedWords;
	private String wordMeanings;
	private String explanation;
	private boolean isFavorite;
	
	public Kagga(String kagga, String dividedWords, String wordMeanings, String explanation, boolean isFavorite) {
		this.kagga = kagga;
		this.dividedWords = dividedWords;
		this.wordMeanings = wordMeanings;
		this.explanation = explanation;
		this.isFavorite = isFavorite;
	}
	
	public String getKagga() {
		return kagga;
	}
	
	public String getDividedWords() {
		return dividedWords;
	}
	
	public String getWordMeanings() {
		return wordMeanings;
	}
	
	public String getExplanation() {
		return explanation;
	}

	public boolean isFavorite() {
		return isFavorite;
	}

	@Override
	public String toString() {
		return kagga+" : "+ dividedWords+" : "+wordMeanings;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(kagga);
		parcel.writeString(dividedWords);
		parcel.writeString(wordMeanings);
		parcel.writeString(explanation);
		parcel.writeInt(isFavorite ? 1 : 0);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static Creator<Kagga> CREATOR = new Creator<Kagga>() {
		@Override
		public Kagga createFromParcel(Parcel parcel) {
			return new Kagga(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readInt()==1);
		}

		@Override
		public Kagga[] newArray(int i) {
			return new Kagga[i];
		}
	};
}
