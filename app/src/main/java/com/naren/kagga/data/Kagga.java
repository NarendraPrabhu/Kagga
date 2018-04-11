package com.naren.kagga.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.os.Parcel;
import android.os.Parcelable;

import com.naren.kagga.db.DatabaseHelper;

@Entity(tableName = DatabaseHelper.TABLE_KAGGA)
public class Kagga implements Parcelable{

	@ColumnInfo(name = DatabaseHelper.COLUMN_KAGGA)
	private String kagga;

	@ColumnInfo(name = DatabaseHelper.COLUMN_DIVIDED_WORDS)
	private String dividedWords;

	@ColumnInfo(name = DatabaseHelper.COLUMN_WORD_MEANINGS)
	private String wordMeanings;

	@ColumnInfo(name = DatabaseHelper.COLUMN_EXPLANATION)
	private String explanation;

	@ColumnInfo(name = DatabaseHelper.COLUMN_FAVORITE)
	private boolean isFavorite;

	@ColumnInfo(name = DatabaseHelper.COLUMN_TYPE)
	private String type;
	
	public Kagga(String kagga, String dividedWords, String wordMeanings, String explanation, boolean isFavorite, String type) {
		this.kagga = kagga;
		this.dividedWords = dividedWords;
		this.wordMeanings = wordMeanings;
		this.explanation = explanation;
		this.isFavorite = isFavorite;
		this.type = type;
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

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return kagga+" : "+ dividedWords+" : "+wordMeanings+" : "+type;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(kagga);
		parcel.writeString(dividedWords);
		parcel.writeString(wordMeanings);
		parcel.writeString(explanation);
		parcel.writeInt(isFavorite ? 1 : 0);
		parcel.writeString(type);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static Creator<Kagga> CREATOR = new Creator<Kagga>() {
		@Override
		public Kagga createFromParcel(Parcel parcel) {
			return new Kagga(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readInt()==1, parcel.readString());
		}

		@Override
		public Kagga[] newArray(int i) {
			return new Kagga[i];
		}
	};
}
