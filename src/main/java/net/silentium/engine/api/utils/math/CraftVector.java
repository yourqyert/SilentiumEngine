package net.silentium.engine.api.utils.math;

import com.comphenix.protocol.wrappers.Vector3F;

public record CraftVector(float x, float y, float z) {

    public Vector3F toVector3f() {
        return new Vector3F(this.x, this.y, this.z);
    }

    public static CraftVector fromVector3f(Vector3F vector3F) {
        return new CraftVector(vector3F.getX(), vector3F.getY(), vector3F.getZ());
    }

}

