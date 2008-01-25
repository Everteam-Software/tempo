#
# Copyright (c) 2005-2007 Intalio inc.
#
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# Contributors:
# Intalio inc. - initial API and implementation
#

namespace "check" do

  desc "Checks license headers."
  task("headers") do
    # Define license headers based on the filename extension.
    licenses = {}
    licenses[".java"] = <<EOF
/**
 * Copyright (c) 2005-2007 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */
EOF
    licenses[".xml2"] = <<EOF
<!--
  ~ Copyright (c) 2005-2007 Intalio inc.
  ~
  ~ All rights reserved. This program and the accompanying materials
  ~ are made available under the terms of the Eclipse Public License v1.0
  ~ which accompanies this distribution, and is available at
  ~ http://www.eclipse.org/legal/epl-v10.html
  ~
  ~ Contributors:
  ~ Intalio inc. - initial API and implementation
  -->
EOF
    licenses[".properties2"] = <<EOF
#
# Copyright (c) 2005-2007 Intalio inc.
#
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# Contributors:
# Intalio inc. - initial API and implementation
#
EOF
    licenses[".bpel"] = licenses[".wsdl"] = licenses[".xsd"] = licenses[".soap"] = licenses['.xpl'] = licenses['.xsl'] = licenses[".xml2"]
    licenses[".rake"] = licenses[".properties2"]
    
    # This also tells us which files to look at.
    extensions = licenses.keys.join(",")
    count = FileList["**/*{#{extensions}}"].inject(0) do |count, filename|
      if File.readlines(filename)[0..3].join !~ /Copyright.*(Intalio|Orbeon)/i
        when_writing "Missing header in #{filename}" do
          # Figure the license from the file, inject it into the file and rewrite it.
          license = licenses[filename.pathmap("%x")]
          if license
            content = File.read(filename)
            if (content =~ /<\?xml .*\?>/)
	      modified = content.sub(/(<\?xml .*\?>\n?)(.*)/m) { "#{$1}#{license}#{$2}" }
            else
              modified = license + "\n" + content
            end
            File.open(filename, "w") { |file| file.write modified }
          else
            puts "Skipping unknown extension for file #{filename}"
          end
          count + 1
        end
      else
        count
      end
    end
    if count > 0
      warn "#{count} files found to have missing headers."
    else
      puts "All #{extensions} files checked and have the license in them."
    end
  end

end
