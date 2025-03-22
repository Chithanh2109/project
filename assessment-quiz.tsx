import { useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Label } from "@/components/ui/label";
import { useToast } from "@/hooks/use-toast";
import { apiRequest } from "@/lib/queryClient";
import { Loader2 } from "lucide-react";
import { useLocation } from "wouter";

const questions = [
  {
    id: "skin_type",
    question: "Bạn mô tả loại da của bạn như thế nào?",
    options: [
      { value: "oily", label: "Da dầu" },
      { value: "dry", label: "Da khô" },
      { value: "combination", label: "Da hỗn hợp" },
      { value: "normal", label: "Da thường" },
    ],
  },
  {
    id: "concerns",
    question: "Vấn đề chính về da mà bạn đang gặp phải?",
    options: [
      { value: "acne", label: "Mụn" },
      { value: "aging", label: "Lão hóa" },
      { value: "pigmentation", label: "Thâm nám" },
      { value: "sensitivity", label: "Da nhạy cảm" },
    ],
  },
  {
    id: "routine",
    question: "Bạn đánh giá quy trình chăm sóc da hiện tại của bạn thế nào?",
    options: [
      { value: "basic", label: "Cơ bản (Rửa mặt & Dưỡng ẩm)" },
      { value: "intermediate", label: "Trung bình (Bao gồm Toner & Serum)" },
      { value: "advanced", label: "Đầy đủ (Quy trình toàn diện)" },
      { value: "none", label: "Không có quy trình thường xuyên" },
    ],
  },
  {
    id: "frequency",
    question: "Bạn có thường xuyên đi spa hoặc chăm sóc da chuyên nghiệp không?",
    options: [
      { value: "regular", label: "Thường xuyên (1-2 lần/tháng)" },
      { value: "occasional", label: "Thỉnh thoảng (1-2 lần/quý)" },
      { value: "rarely", label: "Hiếm khi (1-2 lần/năm)" },
      { value: "never", label: "Chưa từng" },
    ],
  },
];

export default function AssessmentQuiz() {
  const { toast } = useToast();
  const [, navigate] = useLocation();
  const [currentQuestion, setCurrentQuestion] = useState(0);
  const [answers, setAnswers] = useState<Record<string, string>>({});
  const [showResults, setShowResults] = useState(false);

  const assessmentMutation = useMutation({
    mutationFn: async (data: any) => {
      const res = await apiRequest("POST", "/api/skin-assessment", {
        answers: data,
        date: new Date(),
      });
      return res.json();
    },
    onSuccess: (data) => {
      setShowResults(true);
      toast({
        title: "Đánh Giá Hoàn Tất",
        description: "Đề xuất dịch vụ phù hợp với loại da của bạn đã sẵn sàng.",
      });
    },
    onError: (error: Error) => {
      toast({
        title: "Đánh Giá Thất Bại",
        description: error.message,
        variant: "destructive",
      });
    },
  });

  const handleAnswer = (value: string) => {
    const newAnswers = {
      ...answers,
      [questions[currentQuestion].id]: value,
    };
    setAnswers(newAnswers);

    if (currentQuestion < questions.length - 1) {
      setCurrentQuestion(currentQuestion + 1);
    } else {
      assessmentMutation.mutate(newAnswers);
    }
  };

  // Generate recommendations based on answers
  const getRecommendedServices = () => {
    const recommendations = [];

    if (answers.skin_type === "oily") {
      recommendations.push({
        id: 1,
        name: "Liệu trình Điều Trị Da Dầu Chuyên Sâu",
        description: "Giảm dầu thừa, se khít lỗ chân lông và cân bằng độ ẩm cho da."
      });
    }

    if (answers.concerns === "acne") {
      recommendations.push({
        id: 2,
        name: "Trị Liệu Mụn Chuyên Sâu",
        description: "Điều trị mụn, giảm viêm và ngăn ngừa sẹo thâm."
      });
    }

    if (answers.concerns === "aging") {
      recommendations.push({
        id: 3,
        name: "Liệu Trình Trẻ Hóa Da Chuyên Sâu",
        description: "Kích thích collagen, làm mờ nếp nhăn và cải thiện độ đàn hồi của da."
      });
    }

    if (answers.concerns === "pigmentation") {
      recommendations.push({
        id: 4,
        name: "Điều Trị Thâm Nám Chuyên Sâu",
        description: "Giảm sắc tố, làm đều màu da và ngăn ngừa tăng sắc tố."
      });
    }

    if (answers.skin_type === "dry") {
      recommendations.push({
        id: 5,
        name: "Liệu Trình Phục Hồi Độ Ẩm Chuyên Sâu",
        description: "Cung cấp độ ẩm, phục hồi hàng rào bảo vệ da và nuôi dưỡng da khô."
      });
    }

    if (answers.routine === "none" || answers.routine === "basic") {
      recommendations.push({
        id: 6,
        name: "Gói Chăm Sóc Da Cơ Bản",
        description: "Làm sạch sâu, tẩy tế bào chết và cung cấp độ ẩm cần thiết cho da."
      });
    }

    // If no specific recommendations, suggest general service
    if (recommendations.length === 0) {
      recommendations.push({
        id: 7,
        name: "Tư Vấn Da Chuyên Sâu",
        description: "Đánh giá da chi tiết và tư vấn quy trình chăm sóc da phù hợp."
      });
    }

    return recommendations;
  };

  if (showResults) {
    const recommendedServices = getRecommendedServices();
    
    return (
      <div className="min-h-screen p-8 bg-background">
        <Card className="max-w-2xl mx-auto">
          <CardHeader>
            <CardTitle>Kết Quả Đánh Giá Da Của Bạn</CardTitle>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="p-4 bg-muted rounded-lg">
              <h3 className="font-semibold mb-3">Dịch Vụ Được Đề Xuất:</h3>
              <div className="space-y-4">
                {recommendedServices.map((service) => (
                  <div key={service.id} className="border-b pb-4 last:border-b-0 last:pb-0">
                    <h4 className="font-medium text-lg">{service.name}</h4>
                    <p className="text-muted-foreground">{service.description}</p>
                  </div>
                ))}
              </div>
            </div>
            
            <div className="flex flex-col sm:flex-row gap-3">
              <Button 
                className="flex-1"
                onClick={() => navigate("/booking")}
              >
                Đặt Lịch Ngay
              </Button>
              <Button 
                variant="outline"
                className="flex-1"
                onClick={() => {
                  setShowResults(false);
                  setCurrentQuestion(0);
                  setAnswers({});
                }}
              >
                Làm Lại Bài Kiểm Tra
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="min-h-screen p-8 bg-background">
      <Card className="max-w-2xl mx-auto">
        <CardHeader>
          <CardTitle>Đánh Giá Da</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="mb-8">
            <div className="text-sm text-muted-foreground mb-2">
              Câu hỏi {currentQuestion + 1} / {questions.length}
            </div>
            <div className="h-2 bg-muted rounded-full">
              <div 
                className="h-2 bg-primary rounded-full transition-all"
                style={{ 
                  width: `${((currentQuestion + 1) / questions.length) * 100}%` 
                }}
              />
            </div>
          </div>

          <h2 className="text-xl font-semibold mb-6">
            {questions[currentQuestion].question}
          </h2>

          <RadioGroup
            onValueChange={handleAnswer}
            value={answers[questions[currentQuestion].id]}
            className="space-y-4"
          >
            {questions[currentQuestion].options.map((option) => (
              <div key={option.value} className="flex items-center space-x-2 border p-3 rounded-md hover:bg-accent transition-colors">
                <RadioGroupItem value={option.value} id={option.value} />
                <Label htmlFor={option.value} className="flex-1 cursor-pointer">{option.label}</Label>
              </div>
            ))}
          </RadioGroup>

          {assessmentMutation.isPending && (
            <div className="mt-8 flex justify-center">
              <Loader2 className="h-8 w-8 animate-spin" />
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
