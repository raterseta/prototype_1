package com.example.prototype_1.Model
import com.example.prototype_1.Repo.ProfileData;


class ProfileLibrary(){
    fun getProfilesHead(): List<ProfilesHead> {
        return ProfileData.profile
    }
}