package com.example.omegatracker.entity

import com.example.omegatracker.R

enum class HelperContent(
    val text: Int?,
    val imageId: Int?
) {

    STEP_1(
        R.string.auth_helper_content_step_1,
        R.drawable.auth_helper_image_1
    ),

    STEP_2(
        R.string.auth_helper_content_step_2,
        R.drawable.auth_helper_image_2
    ),

    STEP_3(
        R.string.auth_helper_content_step_3,
        R.drawable.auth_helper_image_3
    ),
    STEP_4(
        R.string.auth_helper_content_step_4,
        R.drawable.auth_helper_image_4
    ),
    STEP_5(
        R.string.auth_helper_content_step_5,
        R.drawable.auth_helper_image_5
    ),
    STEP_6(
        R.string.auth_helper_content_step_6,
        R.drawable.auth_helper_image_6
    ),

    STEP_7(
        R.string.auth_helper_content_step_7,
        null
    )

}
