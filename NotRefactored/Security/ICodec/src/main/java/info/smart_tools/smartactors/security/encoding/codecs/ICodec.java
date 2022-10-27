package info.smart_tools.smartactors.security.encoding.codecs;

import info.smart_tools.smartactors.security.encoding.decoders.IDecoder;
import info.smart_tools.smartactors.security.encoding.encoders.IEncoder;

/**
 * Marker-interface for components with encode-decode logic
 */
public interface ICodec extends IEncoder, IDecoder { }
