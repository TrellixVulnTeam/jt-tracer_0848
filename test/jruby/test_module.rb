require 'test/unit'
require 'test/jruby/test_helper'

class ModuleTest < Test::Unit::TestCase
  # include TestHelper

  def test_prepend_features_type_error
    assert_raise(TypeError) do
      Module.new.instance_eval { prepend_features(1) }
    end
  end

end
