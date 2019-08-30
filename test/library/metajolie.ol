include "../AbstractTestUnit.iol"
include "metajolie.iol"

define doTest
{
  with( rq ) {
      .filename = "private/sample_service.ol"
  };
  getInputPortMetaData@MetaJolie( rq )( meta_description );
  if ( #meta_description.input != 1 ) {
      throw( TestFailed, "Expected 1 input port, found " + #meta_description.input )
  };
  if ( meta_description.input.name != "TPort" ) {
      throw( TestFailed, "Expected input port name equal to \"TPort\", found " + meta_description.input.name )
  };
  if ( meta_description.input[0].protocol != "sodep" ) {
      throw( TestFailed, "Expected sodep protocol, found " + meta_description.input[0].protocol )
  };
  if ( #meta_description.input.interfaces != 1 ) {
      throw( TestFailed, "Expected 1 interface, found " + #meta_description.input.interfaces )
  };
  if ( meta_description.input.interfaces.name != "TmpInterface" ) {
      throw( TestFailed, "Expected interface name equal to \"TmpInterface\", found " + meta_description.input.interfaces.name )
  };
  if ( #meta_description.input.interfaces.types != 6 ) {
      throw( TestFailed, "Expected 6 types, found " + #meta_description.input.interfaces.types )
  };
  if ( #meta_description.input.interfaces.operations != 3 ) {
      throw( TestFailed, "Expected 3 operation, found " + #meta_description.input.interfaces.operations )
  };
  if ( meta_description.input.interfaces.operations.operation_name != "tmp" ) {
      throw( TestFailed, "Expected operation_name equal to \"tmp\", found " + meta_description.input.interfaces.operations.operation_name )
  };
  if ( meta_description.input.interfaces.operations[1].operation_name != "tmp3" ) {
      throw( TestFailed, "Expected second operation_name equal to \"tmp3\", found " + meta_description.input.interfaces.operations[1].operation_name )
  }

  getMetaData@MetaJolie( rq )( metadata )
  if ( #metadata.types != 6 ) {
      throw( TestFailed, "Expected 6 types in metadata, found " + #metadata.types )
  }
  if ( #metadata.interfaces != 1 ) {
      throw( TestFailed, "Expected 1 interface in metadata, found " + #metadata.interfaces )
  }
   if ( #metadata.interfaces.operations != 3 ) {
      throw( TestFailed, "Expected 3 operations in metadata.interfaces[0], found " + #metadata.interfaces.operations )
  }
  if ( #metadata.input != 1 ) {
      throw( TestFailed, "Expected 1 input in metadata, found " + #metadata.input )
  }
}
