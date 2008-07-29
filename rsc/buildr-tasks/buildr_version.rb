IS_BUILDR_2 = (Buildr::VERSION[2..2] == "2")
IS_BUILDR_3 = (Buildr::VERSION[2..2] == "3")

if IS_BUILDR_2 then 
  require "java/java" 
  require "java/ant"
else 
  require "buildr/java"
  require "buildr/java/ant"
end

def update_java_classpath requires
  if IS_BUILDR_2 then
    Java.wrapper.classpath << requires
  else
    Java.classpath << requires
  end
end