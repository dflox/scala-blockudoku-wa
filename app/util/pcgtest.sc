import util.{PCG32Random, ProbabilityList}

var pcg = PCG32Random(42L, 54L)

for i <- 1 to 5 do
  println(s"Random Int ${i}: ${pcg.nextInt()}")
  
var elemProbabilities = ProbabilityList(Seq(
  ("Small", 0.5),
  ("Medium", 0.3),
  ("Large", 0.2)
))

var randomValue = pcg.nextFloat()

elemProbabilities.getRandomItem(randomValue)
elemProbabilities.getRandomItem(0.1)



val iterations = 1000000
var countPerValue = scala.collection.mutable.Map[Int, Int]()
for i <- 0 until iterations do
  pcg.nextValBetween(0, 3) match
    case value =>
      countPerValue(value) = countPerValue.getOrElse(value, 0) + 1
      
countPerValue.toSeq.sortBy(_._1).foreach { case (value, count) =>
  val percentage = (count.toDouble / iterations) * 100
  println(f"Value: $value, Count: $count, Percentage: $percentage%.2f%%")
}