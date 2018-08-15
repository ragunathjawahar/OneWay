package io.redgreen.oneway.android.fixtures;

import android.os.Parcel;
import android.os.Parcelable;

public class Student implements Parcelable {
  public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    public Student createFromParcel(Parcel in) {
      return new Student(in);
    }

    public Student[] newArray(int size) {
      return new Student[size];
    }
  };

  private final String name;

  public Student(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @SuppressWarnings("WeakerAccess")
  public Student(Parcel in) {
    this.name = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.name);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Student student = (Student) o;

    return name != null ? name.equals(student.name) : student.name == null;
  }

  @Override
  public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "Student{" +
        "name='" + name + '\'' +
        '}';
  }
}
