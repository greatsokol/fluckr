package com.greatsokol.fluckr.presenters;

import com.greatsokol.fluckr.models.api.Photo;
import com.greatsokol.fluckr.models.api.Photos;
import com.greatsokol.fluckr.views.ImageListItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class Interactor {
    private Interactor(){}

    static ArrayList<ImageListItem> Translate(Date date, Photos flickrPhotos){
        List<Photo> PhotosArray = flickrPhotos.getPhoto();
        int page = flickrPhotos.getPage();
        ArrayList<ImageListItem> imageListItems = new ArrayList<>();
        int photosNumber = PhotosArray.size();
        for(int i=0; i<photosNumber; i++){
            Photo photo = PhotosArray.get(i);
            try {
                imageListItems.add(new ImageListItem(
                        date,
                        flickrPhotos.getPages(),
                        page,
                        i, photo));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(page==1 && imageListItems.size()>0) {
            ImageListItem.ListItemPageParams pageParams = imageListItems.get(0).getPageParams();
            imageListItems.add(0, new ImageListItem(pageParams.getDate(), pageParams.getPage()));
        }

        return imageListItems;
    }
}
