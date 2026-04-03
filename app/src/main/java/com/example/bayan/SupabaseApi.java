package com.example.bayan;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface SupabaseApi {

    // obtenir organisation par access_code
    @GET("Organisations?select=*")
    Call<List<Organisation>> getOrganisationByCode(

            @Query("access_code") String accessCode
    );

    //get news par org_id
    @GET("news?select=*")
    Call<List<News>> getNewsByOrgId(
            @Query("org_id") String orgId,
            @Query("order") String order
    );
}