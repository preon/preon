public class Foo {

    private String foo;

    private int bar = 5;

    public Foo(String foo, int bar) {
	this.foo = foo;
	this.bar = bar;
    }

    public String getFoo() {
	return foo;
    }

    public int getBar() {
	return bar;
    }

    public String toString() {
	return foo + bar;
    }

}
