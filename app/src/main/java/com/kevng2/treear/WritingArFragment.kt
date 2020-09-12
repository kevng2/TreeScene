package com.kevng2.treear

import com.google.ar.sceneform.ux.ArFragment

class WritingArFragment : ArFragment() {
    override fun getAdditionalPermissions(): Array<String?> {
        val additionalPermissions: Array<String> = super.getAdditionalPermissions()
        val permissionLength = additionalPermissions.size
        val permission = arrayOfNulls<String>(permissionLength + 1)
        permission[0] = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (permissionLength > 0) {
            System.arraycopy(
                additionalPermissions, 0, permission, 1,
                additionalPermissions.size
            )
        }
        return permission
    }
}
