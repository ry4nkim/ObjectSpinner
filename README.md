[![API](https://img.shields.io/badge/API-19%2B-blue.svg?style=flat)](https://developer.android.com/reference/android/os/Build.VERSION_CODES#KITKAT) [![LICENSE](https://img.shields.io/:License-Apache%202.0-blue.svg)](LICENSE)

Object Spinner (Searchable)
---

ObjectSpinner is an Android spinner library for object selection and supports searching.

<br>
<p align="center">
  <img src="https://raw.githubusercontent.com/ry4nkim/ObjectSpinner/master/example.gif" width="350">
</p>
<br>

Download [![Download](https://api.bintray.com/packages/kr-ry4nkim/maven/objectspinner/images/download.svg?version=1.0.0)](https://bintray.com/kr-ry4nkim/maven/objectspinner/1.0.0/link)
--------

Download [the latest AAR](https://dl.bintray.com/kr-ry4nkim/maven/kr/ry4nkim/objectspinner/1.0.0/objectspinner-1.0.0.aar) or grab via Gradle:

```groovy
implementation 'kr.ry4nkim.ObjectSpinner:1.0.0'
```
or Maven:
```xml
<dependency>
  <groupId>kr.ry4nkim</groupId>
  <artifactId>objectspinner</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```

Usage
-----

1. Add the ObjectSpinner to your layout XML:

```xml
<kr.ry4nkim.ObjectSpinner
    android:id="@+id/spinner"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"/>
```

2. Implement ObjectSpinner.Delegate on your object to override the getSpinnerDelegate() method to return the spinner's delegate:

```java
public class YourObject implements ObjectSpinner.Delegate {
  private String mCode;
  private String mName;
  ...

  @Override
  public String getSpinnerDelegate() {
    return mName;
  }
  ...
}
```

3. Add item list to the spinner and listen for item select:

```java
ObjectSpinner<YourObject> mObjectSpinner = findViewById(R.id.spinner);

List<YourObject> itemList = new ArrayList<>();
itemList.add(new YourObject("obj0001", "Object 1"));
itemList.add(new YourObject("obj0002", "Object 2"));
itemList.add(new YourObject("obj0003", "Object 3"));
mObjectSpinner.setItemList(itemList);

mObjectSpinner.setOnItemSelectedListener((view, position, item) -> {
  Snackbar.make(view, "Selected Object : " + item, Snackbar.LENGTH_SHORT).show();
});

mObjectSpinner.setOnNothingSelectedListener(view -> {
  Log.i(TAG, "onNothingSelected");
});
```

You can add attributes to customize the view. Available attributes:

| name                           | type      | info                                           |
|--------------------------------|-----------|------------------------------------------------|
| padding                        | dimension | set the padding of the spinner                 |
| padding_left                   | dimension | set the left padding of the spinner            |
| padding_top                    | dimension | set the top padding of the spinner             |
| padding_right                  | dimension | set the right padding of the spinner           |
| padding_bottom                 | dimension | set the bottom padding of the spinner          |
| text_size                      | dimension | set the text size of the spinner               |
| text_color                     | color     | set the text color of the spinner              |
| background_color               | color     | set the background color of the spinner        |
| hint                           | string    | set the hint of the spinner                    |
| hint_color                     | color     | set the hint color of the spinner              |
| arrow_color                    | color     | set the arrow color of the spinner             |
| shadow                         | boolean   | set to false to hide the shadow of the spinner |
| list_max_height                | dimension | set the max height of the spinner list         |
| list_empty_text                | string    | set the empty list text of the spinner list    |
| item_padding                   | dimension | set the padding of the spinner item            |
| item_padding_left              | dimension | set the left padding of the spinner item       |
| item_padding_top               | dimension | set the top padding of the spinner item        |
| item_padding_right             | dimension | set the right padding of the spinner item      |
| item_padding_bottom            | dimension | set the bottom padding of the spinner item     |
| item_text_size                 | dimension | set the text size of the spinner item          |
| item_text_color                | color     | set the text color of the spinner item         |
| item_background_color          | color     | set the background color of the spinner item   |
| selected_item_text_size        | dimension | set the padding of the spinner selected item   |
| selected_item_text_color       | color     | set the padding of the spinner selected item   |
| selected_item_background_color | color     | set the padding of the spinner selected item   |
| search_text_color              | color     | set the search text color of the spinner       |
| search_icon_color              | color     | set the search icon color of the spinner       |
| search_background_color        | color     | set the search background color of the spinner |
| searchable                     | boolean   | set to false to hide the search of the spinner |

Acknowledgements
----------------

[Material Spinner](https://github.com/jaredrummler/MaterialSpinner) by Jared Rummler

License
--------
    Copyright (C) 2019 ry4nkim

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.