# GallerySimple
Simple application to manager the pictures and videos.

- View all pictures/video in phone storage
- Create, edit, delete an album
- Add picture/video to an album, delete picture/video from an album
- Mark a picture/video as favorite
- Move deleted pictures/videos to Recycle Bin
- User should be able to restore pictures/videos in Recycle Bin

*Application work fine on version android not Scoped storage. Target version 24 - 26.
All techology used:
- MVVP architechture.
- View binding, View Model, Live Data, android navigation, Glide (load and show image), ExoPlayer2 (play video)
- Room database, ContentResolver (load all images and video)
- RxJava/RxAdnroid


![SplashScreen](https://user-images.githubusercontent.com/38234174/173270445-ed600fc5-b662-4271-b528-e7d63312cc21.PNG)

**Splash screen**: loading all the image/video from storage and sync to room database. (the new pic/video will load to application when app launch).


![Main-all](https://user-images.githubusercontent.com/38234174/173270911-0baa0b39-a5ea-49a7-b1aa-2f5f79ccb8c6.png)

**Main screen - Pictures**: Show all the pictures and videos as list, with simple information like favorite, thumbnail and duration (video).


![album_function](https://user-images.githubusercontent.com/38234174/173271894-ed75fb9e-2c25-4b2a-be57-519403a34862.png)
**Main screen - Albums**: Show all albums and create/edit/delete album.


![detail_screen_func](https://user-images.githubusercontent.com/38234174/173271494-bbd849da-b905-48ef-8e06-60175cd3531b.png)
**Detail screen**: Show imgage or play video, show information, check/uncheck favorite, add to album, delete from album and delete (move to recyce bin).


![album_detail_func](https://user-images.githubusercontent.com/38234174/173272125-73e990e1-953f-43d1-aa22-39d8bf8d9526.png)
**Album detail sreen**: Show all item of album. When this is Recycle Bin, provide feature restore image/video.

Future feature: Apply Dagger, Hilt for Dependence Injecttion.
