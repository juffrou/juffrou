What's new in Juffrou 2.0.8

juffrou-reflect now supports "is" getters and setters for primitive boolean properties acording to the oracle specification, like in the following example:
private boolean isActive;

//Getter
public boolean isActive() {...}

//Setter
public void setActive(boolean isActive) {...}

slight improvements in juffrou-reflect

better documentation