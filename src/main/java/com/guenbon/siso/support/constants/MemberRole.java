package com.guenbon.siso.support.constants;

import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.AuthErrorCode;

public enum MemberRole {
    MEMBER,
    ADMIN;

    public static MemberRole from(String value) {
        for (MemberRole role : MemberRole.values()) {
            if (role.toString().equals(value)) {
                return role;
            }
        }
        throw new CustomException(AuthErrorCode.INVALID_ROLE);
    }
}
