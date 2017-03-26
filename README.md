# freg -> face
- 1, please install android ndk in you computer
- 2, follow http://sarl-tokyo.com/wordpress/?p=553 to make the project run

# how to build opencv 3 for android with extra module
  - how to build opencv for android https://github.com/opencv/opencv/wiki/Building_OpenCV4Android_from_trunk
  - how to build opencv with extra module for android https://github.com/opencv/opencv_contrib
  - how to build multi release arm64-v8a, https://github.com/opencv/opencv/blob/master/platforms/android/android.toolchain.cmake

# how to run the test
  - copy freg_test_faces 5 faces to folder Environment.getExternalStorageDirectory() in your phone
  - run the apk
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
  
