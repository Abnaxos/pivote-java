/*
 * Copyright 2012 Piratenpartei Schweiz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.piratenpartei.pivote.rpc;

import java.util.Map;

import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;

import ch.raffael.util.common.logging.LogUtil;


/**
* @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
*/
public enum ErrorCode {

    UNKNOWN(0, 0),

    ARGUMENT_NULL(0, 1),
    ARGUMENT_OUT_OF_RANGE(0, 2),
    BAD_SERIALIZABLE_FORMAT(0, 3),
    INVALID_CERTIFICATE(0, 4),
    WRONG_STATUS_FOR_OPERATION(0, 5),
    REQUEST_SIGNATURE_INVALID(0, 6),
    NO_AUTHORIZED_ADMIN(0, 7),
    BAD_VOTING_MATERIAL(0, 8),
    INVALID_SIGNATURE(0, 9),
    INVALID_SIGNATURE_REQUEST(0, 10),
    SERVER_CERTIFICATE_INVALID(0, 11),
    CANCELED_BY_USER(0, 12),

    AUTHORITY_COUNT_OUT_OF_RANGE(1, 1),
    THRESHOLD_OUT_OF_RANGE(1, 2),
    OPTION_COUNT_OUT_OF_RANGE(1, 3),
    MAX_VOTA_OUT_OF_RANGE(1, 4),
    OPTION_COUNT_MISMATCH(1, 5),
    P_IS_NO_PRIME(1, 6),
    P_IS_NO_SAFE_PRIME(1, 7),
    Q_IS_NO_PRIME(1, 8),
    AUTHORITY_COUNT_MISMATCH(1, 9),
    AUTHORITY_INVALID(1, 10),

    NO_VOTING_WITH_ID(2, 1),

    NO_AUTHORITY_WITH_CERTIFICATE(3, 1),

    ALREADY_VOTED(4, 1),
    VOTE_SIGNATURE_NOT_VALID(4, 2),
    NO_VOTER_CERTIFICATE(4, 3),
    INVALID_VOTE_RECEIPT(4, 4),
    BAD_GROUP_IN_CERTIFICATE(4, 5),
    INVALID_ENVELOPE(4, 6),
    INVALID_ENVELOPE_BAD_DATE_TIME(4, 7),
    INVALID_ENVELOPE_BAD_VOTER_ID(4, 8),
    INVALID_ENVELOPE_BAD_BALLOT_COUNT(4, 9),
    INVALID_ENVELOPE_BAD_PROOF_COUNT(4, 10),
    INVALID_ENVELOPE_BAD_VOTE_COUNT(4, 11),

    SIGNATURE_REQUEST_INVALID(5, 1),
    SIGNATURE_REQUEST_RESPONDED(5, 2),
    SIGNATURE_REQUEST_NOT_FOUND(5, 3),

    SIGNATURE_REQUEST_NOT_FROM_CA(6, 1),

    NOT_AUTHORIZED_AUTHORITY(7, 1),
    ALREADY_ENOUGH_AUTHORITIES(7, 2),
    AUTHORITY_ALREADY_IN_VOTING(7, 3),
    AUTHORITY_HAS_ALREADY_DEPOSITED(7, 4),

    PARTIAL_DECIPHER_BAD_SIGNATURE(8, 1),
    PARTIAL_DECIPHER_BAD_ENVELOPE_COUNT(8, 2),
    PARTIAL_DECIPHER_BAD_ENVELOPE_HASH(8, 3),

    SHARE_RESPONSE_BAD_SIGNATURE(9, 1),
    SHARE_RESPONSE_WRONG_AUTHORITY(9, 2),
    SHARE_RESPONSE_NOT_ACCEPTED(9, 3),
    SHARE_RESPONSE_PARAMETERS_DONT_MATCH(9, 4),

    COMMAND_NOT_FROM_ADMIN(19, 1),
    COMMAND_NOT_ALLOWED_IN_STATUS(19, 2)
    ;

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LogUtil.getLogger();

    private static int CAT = 1000000;
    private static Map<Integer, ErrorCode> byNumeric;
    static {
        ImmutableMap.Builder<Integer, ErrorCode> builder = ImmutableMap.builder();
        for ( ErrorCode c : ErrorCode.values() ) {
            builder.put(c.numeric(), c);
        }
        byNumeric = builder.build();
    }
    private final int numeric;

    ErrorCode(int cat, int numeric) {
        this.numeric = cat * 1000000 + numeric;
    }

    public int numeric() {
        return numeric;
    }

    public int category() {
        return numeric / CAT;
    }

    public int subcode() {
        return numeric % CAT;
    }

    public static ErrorCode byNumeric(int n) {
        ErrorCode code = byNumeric.get(n);
        if ( code == null ) {
            log.error("Unknown error code: {}", n);
            return UNKNOWN;
        }
        else {
            return code;
        }
    }

}
