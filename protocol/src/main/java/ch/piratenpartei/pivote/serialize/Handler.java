package ch.piratenpartei.pivote.serialize;

import java.io.IOException;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface Handler {

    Object read(DataInput input) throws IOException;

}

// containers:
//
// List<T>
// Map<K, V>
// Signed<T>
// Secure<T> (Authenticated & Encrypted == Signed & Encrypted (?))
// Encrypted<T>
// Tuple<T...>; it should be possible to replace this with a type. Usages:
//     - Pirate.PiVote.Rpc.FetchVotingMaterialVoterResponse
//     (yes, that's the only usage)
//
// Signed, Secure, Encrypted are never used in maps and only as elements in lists (i.e.
// List<Signed<T>> exists, but no Signed<List<T>>
//
// Conclusion: Signed, Secure and Encrypted may be implemented as flags for fields.
