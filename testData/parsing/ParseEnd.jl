#=
ParseEnd:
- Julia version: 
- Author: ice1000
- Date: 2018-03-19
=#

a=[1, 2, 3]
a[1:end]
a[end:1]
a[end-1]
a[1-end]
a[-1-end]
a[1]
a[end]
a[1-end+1]
a[1+end-1]
a[2*end]

while a isa Array
    if a isa Array
        println(a[end])
        a[2*end]
    end
    function test_end()
        a = [x for x in [1, 4, 0]]
        function test_end()
            a = [x for x in [1, 4, 0]]
        end
    end
end

println(a)