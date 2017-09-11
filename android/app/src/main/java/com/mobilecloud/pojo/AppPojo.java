package com.mobilecloud.pojo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hailor on 2017/6/30.
 */

public class AppPojo {
    private Integer id;
    private String name;
    private String currentVersion;
    private String url;
    private BundlePojo mainBundle;
    private Map<String,BundlePojo> bundles= new HashMap<String,BundlePojo>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public BundlePojo getMainBundle() {
        return mainBundle;
    }

    public void setMainBundle(BundlePojo mainBundle) {
        this.mainBundle = mainBundle;
    }

    public Map<String, BundlePojo> getBundles() {
        return bundles;
    }

    public void setBundles(Map<String, BundlePojo> bundles) {
        this.bundles = bundles;
    }

    @Override
    public String toString() {
        return "AppPojo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", currentVersion='" + currentVersion + '\'' +
                ", url='" + url + '\'' +
                ", mainBundle=" + mainBundle +
                ", bundles=" + bundles +
                '}';
    }
}
