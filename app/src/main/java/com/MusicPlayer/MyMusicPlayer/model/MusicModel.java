package com.MusicPlayer.MyMusicPlayer.model;

public class MusicModel extends BaseModel {

    private String musicId;
    private String name;
    private String poster;
    private String path;
    private String author;
    private String duration;//歌曲时长
    private long endTime;//结束时间
    //    private String remark="";







    public MusicModel() {
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public MusicModel(String musicId, String name, String poster, String path, String author, String duration, long endTime) {
        this.musicId = musicId;
        this.name = name;
        this.poster = poster;
        this.path = path;
        this.author = author;
        this.duration = duration;
        this.endTime = endTime;
    }

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }



}
