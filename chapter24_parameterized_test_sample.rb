
def test_extref
  # setup
  sourceXml = "<extref id='abc'/>"
  expectedHtml = "<a href='abc.html'>abc</a>"
  mocFile = MockFile.new
  @handler = setupHandler(sourceXml, mockFile)
  # execute
  @handler.printBodyContents
  # verify
  assert_equals_html( expectedHtml, mockFile.output,
                      "extref: html output")
end

def testTestterm_normal
  sourceXml = "<testterm id='abc'/>"
  expectedHtml = "<a href='abc.html'>abc</a>"
  mockFile = Mockfile.new
  @handler = setupHandler(sourceXml, mockFile)
  @handler.printBodyContents
  assert_equals_html( expectedHtml, mockFile.output,
                      "testterm: html output")
end

def testTestterm_plural
  sourceXml = "<testterms id='abc'/>"
  expectedHtml = "<a href='abc.html'>abcs</a>"
  mockFile = Mockfile.new
  @handler = setupHandler(sourceXml, mocFile)
  @handler.printBodyContents
  assert_equals_html( expectedHtml, mocFile.output,
                      "testterms: html output")
end

def test_extref
  sourceXml = "<extref id='abc' />"
  expectedHtml = "<a href='abc.html'>abc</a>"
  generateAndVerifyHtml(sourceXml, expectedHtml, "<extref>")
end

def test_testterm_normal
  sourceXml = "<testterm id='abc' />"
  expectedHtml = "<a href='abc.html'>abc</a>"
  generateAndVerifyHtml(sourceXml, expectedHtml, "<testterm>")
end

def test_testterm_plural
  sourceXml = "<testterms id='abc' />"
  expectedHtml = "<a href='abc.html'>abc</a>"
  generateAndVerifyHtml(sourceXml, expectedHtml, "<plural>")
end

def generateAndVerifyHtml( sourceXml, expectedHtml,
                           message, &block)
  mockFile = MockFile.new
  sourceXml.delete!("\t")
  @handler = setupHandler(sourceXml, mockFile)
  block.call unless block == nil
  @handler.printBodyContents
  actual_html = mockFile.output
  assert_equals_html( expectedHtml,
                      actual_html,
                      message + "html output")
  actual_html
end

