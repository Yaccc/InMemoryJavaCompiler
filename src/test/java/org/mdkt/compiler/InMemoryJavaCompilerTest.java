package org.mdkt.compiler;

import java.util.Map;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class InMemoryJavaCompilerTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void compile_WhenTypical() throws Exception {
		StringBuffer sourceCode = new StringBuffer();

		sourceCode.append("package org.mdkt;\n");
		sourceCode.append("public class HelloClass {\n");
		sourceCode.append("   public String hello() { return \"hello\"; }");
		sourceCode.append("}");

		Class<?> helloClass = InMemoryJavaCompiler.newInstance().compile("org.mdkt.HelloClass", sourceCode.toString());
		Assert.assertNotNull(helloClass);
		Assert.assertEquals(1, helloClass.getDeclaredMethods().length);
	}

	@Test
	public void compileAll_WhenTypical() throws Exception {
		String cls1 = "public class A{ public B b() { return new B(); }}";
		String cls2 = "public class B{ public String toString() { return \"B!\"; }}";

		Map<String, Class<?>> compiled = InMemoryJavaCompiler.newInstance().addSource("A", cls1).addSource("B", cls2).compileAll();

		Assert.assertNotNull(compiled.get("A"));
		Assert.assertNotNull(compiled.get("B"));

		Class<?> aClass = compiled.get("A");
		Object a = aClass.newInstance();
		Assert.assertEquals("B!", aClass.getMethod("b").invoke(a).toString());
	}

	@Test
	public void compile_WhenSourceContainsInnerClasses() throws Exception {
		StringBuffer sourceCode = new StringBuffer();

		sourceCode.append("package org.mdkt;\n");
		sourceCode.append("public class HelloClass {\n");
		sourceCode.append("   private static class InnerHelloWorld { int inner; }\n");
		sourceCode.append("   public String hello() { return \"hello\"; }");
		sourceCode.append("}");

		Class<?> helloClass = InMemoryJavaCompiler.newInstance().compile("org.mdkt.HelloClass", sourceCode.toString());
		Assert.assertNotNull(helloClass);
		Assert.assertEquals(1, helloClass.getDeclaredMethods().length);
	}

	@Test
	public void compile_whenError() throws Exception {
		thrown.expect(CompilationException.class);
		thrown.expectMessage("Unable to compile the source");
		StringBuffer sourceCode = new StringBuffer();

		sourceCode.append("package org.mdkt;\n");
		sourceCode.append("public classHelloClass {\n");
		sourceCode.append("   public String hello() { return \"hello\"; }");
		sourceCode.append("}");
		InMemoryJavaCompiler.newInstance().compile("org.mdkt.HelloClass", sourceCode.toString());
	}
}
