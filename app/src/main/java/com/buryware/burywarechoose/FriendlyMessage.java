/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * CHOOSE is written by Steve Stansbury
 *
 * Created May 25, 2018 by the Buryware.
 */
package com.buryware.burywarechoose;

public class FriendlyMessage {

    private String mMsgID;
    private String mUsername;
    private String timeStamp;
    private String mEmail;
    private String mLevel;
    private String mWins;
    private String mLoses;
    private String mDate;
    private String mTotalTime;
    private String mTourneyDate;
    private String mEntryAmount;
    private String mHistory;

    public FriendlyMessage() {
    }

    public FriendlyMessage(String msgID, String userName, String email, String Level, String Wins, String Loses, String Date, String timeStamp, String TotalTime,
                           String TourneyDate, String History) {

        this.mMsgID = msgID;
        this.mUsername = userName;
        this.mEmail = email;
        this.mLevel = Level;
        this.mWins = Wins;
        this.mLoses = Loses;
        this.mDate = Date;

        this.timeStamp = timeStamp;
        this.mTotalTime = TotalTime;
        this.mTourneyDate = TourneyDate;
        this.mHistory = History;
    }

    public String getId() {
        return mMsgID;
    }

    public void setId(String id) {
        this.mMsgID = id;
    }

    public String getTimeStamp()  {
        return this.timeStamp;
    }

    public void setTimeStamp(String timestamp) {
        this.timeStamp = timestamp;
    }

    public String getTotalTime()  {
        return this.mTotalTime;
    }

    public void setTotalTime(String totaltime)  {
        this.mTotalTime = totaltime;
    }

    public String getUserName()  {
        return this.mUsername;
    }

    public void setUserName(String username)  {
        this.mUsername = username;
    }

    public String getDate()  {
        return this.mDate;
    }

    public void setDate(String date)  {
        this.mDate = date;
    }

    public String getEmail()  {
        return this.mEmail;
    }

    public void setEmail(String email)  {
        this.mEmail = email;
    }

    public String getLevel()  {
        return this.mLevel;
    }

    public void setLevel(String level)  {
        this.mLevel = level;
    }

    public String getWins()  {
        return this.mWins;
    }

    public void setWins(String wins)  {
        this.mWins = wins;
    }

    public String getLoses()  {
        return this.mLoses;
    }

    public void setLoses(String loses)  {
        this.mLoses = loses;
    }

    public String getEntryAmount()  {
        return this.mEntryAmount;
    }

    public void setEntryAmount(String entryamount)  {
        this.mEntryAmount = entryamount;
    }

    public String getTourneyDate()  {
        return this.mTourneyDate;
    }

    public void setTourneyDate(String tourneydate)  {
        this.mTourneyDate = tourneydate;
    }

    public String getHistory()  {
        return this.mHistory;
    }

    public void setHistory(String history)  {
        this.mHistory = history;
    }
}
