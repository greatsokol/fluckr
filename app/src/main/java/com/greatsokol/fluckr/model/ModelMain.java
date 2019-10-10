package com.greatsokol.fluckr.model;

import com.greatsokol.fluckr.contract.ContractMain;
import com.greatsokol.fluckr.FluckrApp;
import com.greatsokol.fluckr.view.ImageListItem;
import com.greatsokol.fluckr.etc.ConstsAndUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModelMain implements ContractMain.Model {


    private void __loadPage(final String searchFor,
                            final Date date,
                            final int page,
                            final int numberOnPage,
                            final boolean bAddDateHeader,
                            final boolean bAddItemsAtBottom,
                            final boolean bLoadPrevPageAfterFinish,
                            final OnResponseCallback onResponseCallback){
        FluckrApp.getApi().getList(
                "dcfa7bcdfe436387cefa172c2d3dc2ae",
                ConstsAndUtils.DateToStr_yyyy_mm_dd(date),
                24,
                page,
                "description,url_t,url_m,url_n,url_b,url_k,url_h",
                "json",
                1).enqueue(new Callback<FlickrInterestingnessImageList>() {
            @Override
            public void onResponse(Call<FlickrInterestingnessImageList> call, Response<FlickrInterestingnessImageList> response) {
                assert response.body() != null;
                Photos photos = response.body().getPhotos();
                List<Photo> PhotosArray = photos.getPhoto();
                ArrayList<ImageListItem> imageListItems = new ArrayList<>();
                int photosNumber = PhotosArray.size();
                for(int i=0; i<photosNumber; i++){
                    Photo photo = PhotosArray.get(i);
                    try {
                        imageListItems.add(new ImageListItem(
                                date,
                                photos.getPages(),
                                photos.getPage(),
                                i, photo));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //TODO make interactor
                if(bAddDateHeader && imageListItems.size()>0) {
                    ImageListItem item = imageListItems.get(0);
                    imageListItems.add(0, new ImageListItem(item.getDate(), item.getPage()));
                }

                if(bAddItemsAtBottom) {
                    //addItemsAtBottom(imageListItems);
                    //RemoveObsoletePagesUpper(date, page);
                }
                else {
                    //AddItemsUpper(imageListItems);
                    //RemoveObsoletePagesAtBottom(date, page);
                }

                //if (getItemCount()==0)
                //_showSnack(viewToShowSnackbar, "No results");
                //stopLoading();

                // load previous page when run app with empty
                // list at some previous date and page:

                //if(bLoadPrevPageAfterFinish)
                    //loadUpperPage(viewToShowSnackbar, searchFor);
                onResponseCallback.onResponse(imageListItems);
            }

            @Override
            public void onFailure(Call<FlickrInterestingnessImageList> call, Throwable t) {
                t.printStackTrace();
                onResponseCallback.onFailure(t.getMessage());
            }
        });
    }

    @Override
    public void loadUpperPage(String searchFor) {

    }

    @Override
    public void loadLowerPage(String searchFor) {

    }

    @Override
    public void loadInitialPage(Date savedDate, int savedPage, int savedItemNumber, String searchForThis, OnResponseCallback onResponseCallback) {

        /*Date savedCurrentPageDate
                = new Date(prefs.getLong(ConstsAndUtils.TAG_DATE_TO_VIEW,
                ConstsAndUtils.DecDate(ConstsAndUtils.CurrentGMTDate()).getTime()));
        int savedCurrentPageNumber = prefs.getInt(ConstsAndUtils.TAG_PAGE_TO_VIEW,1);
        int savedCurrentItemNumberOnPage = prefs.getInt(ConstsAndUtils.TAG_NUMBER_ON_PAGE,1); */

        __loadPage(searchForThis,
                savedDate,
                savedPage,
                savedItemNumber,
                savedPage==1,
                true,
                true,
                onResponseCallback);
    }
}
