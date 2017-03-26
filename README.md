# freg -> face recognition
- 1, please install android ndk in you computer
- 2, follow http://sarl-tokyo.com/wordpress/?p=553 to make the project run

# how to build opencv 3 for android with extra module
  - how to build opencv for android https://github.com/opencv/opencv/wiki/Building_OpenCV4Android_from_trunk
  - how to build opencv with extra module for android https://github.com/opencv/opencv_contrib
  - how to build multi release arm64-v8a, https://github.com/opencv/opencv/blob/master/platforms/android/android.toolchain.cmake

# how to run the demo
  - copy freg_test_faces 5 faces to folder Environment.getExternalStorageDirectory() in your phone
  - install run the freg-release.apk
  - click FIND WHO
  - check the result image and confidence(確度)
# some points
  - faces_test_library was the The Database of Faces of AT&T
    http://www.cl.cam.ac.uk/research/dtg/attarchive/facedatabase.html
  - to change the algorithm just make  FaceRecognizer rec = Face.createEigenFaceRecognizer();
        to FaceRecognizer rec = Face.createFisherFaceRecognizer();
        or FaceRecognizer rec = Face.createLBPHFaceRecognizer();
  - confidenceの判断標準は要調査
  - the apk files was in freg/freg/freg-release.apk
  - the jks file for making apk is /freg/freg.jks
  - jks's password is fregfreg
# some reference url
 - http://docs.opencv.org/trunk/dd/d65/classcv_1_1face_1_1FaceRecognizer.html
 - http://www.cl.cam.ac.uk/research/dtg/attarchive/facedatabase.html
 - http://www.emgu.com/forum/viewtopic.php?t=4175
 - http://docs.opencv.org/3.2.0/da/d60/tutorial_face_main.html#tutorial_face_appendix_csv
 - https://github.com/danyf90/FaceRecognizer/blob/master/src/com/eim/facerecognition/EIMFaceRecognizer.java
