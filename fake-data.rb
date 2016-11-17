require 'ffaker'

40.times do
puts  [FFaker::Name.first_name ,
   FFaker::Name.last_name ,
   FFaker::Internet.email ].join("\t")
end
