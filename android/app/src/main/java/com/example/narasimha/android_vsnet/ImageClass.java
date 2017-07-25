package com.example.narasimha.android_vsnet;

/**
 * Created by narasimha on 18/7/17.
 */

public class ImageClass {
    private String image_file_type_camera;
    private String image_file_type_gallery;
    public boolean is_type_camera;
    public void setImage_file_type_camera(String image_file_type_camera) {
        this.image_file_type_camera = image_file_type_camera;
        is_type_camera=true;
    }

    public void setImage_file_type_gallery(String image_file_type_gallery) {
        this.image_file_type_gallery = image_file_type_gallery;
        is_type_camera=false;
    }

    public String getImage_file_type_gallery() {
        return image_file_type_gallery;
    }

    public String getImage_file_type_camera() {
        return image_file_type_camera;
    }

}
